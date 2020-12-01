package io.renren.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import io.renren.config.MongoManager;
import io.renren.entity.mongo.MongoDefinition;
import io.renren.entity.mongo.Type;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * @author: gxz  514190950@qq.com
 **/
public class MongoScanner {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private MongoCollection<Document> collection;

    final private int scanCount;

    private List<String> colNames;

    private MongoDefinition mongoDefinition;


    private final static int[] TYPE = {3, 16, 18, 8, 9, 2, 1};

    private final static int ARRAY_TYPE = 4;

    private final static int MAX_COUNT = 200000;

    private final static int DEFAULT_COUNT = 100000;


    public MongoScanner(MongoCollection<Document> collection) {
        this.collection = collection;
        this.scanCount = DEFAULT_COUNT;
        scan();
    }

    private void scan() {
        // 初始化
        initColNames();
        // 解析屬性值
        mongoDefinition = scanType();
        MongoManager.putInfo(collection.getNamespace().getCollectionName(), mongoDefinition);
        // 解析完成之後釋放链接资源
        this.collection = null;

    }

    public MongoDefinition getProduct() {
        return mongoDefinition;
    }


    /**
     * 功能描述:分組發送聚合函數(獲得一級屬性名)
     *
     * @author : gxz
     */
    public List<String> groupAggregation(Integer skip, Integer limit) throws MongoCommandException {
        skip = skip == null ? 0 : skip;
        limit = limit == null ? scanCount : limit;
        MongoCollection<Document> collection = this.collection;
        BasicDBObject $project = new BasicDBObject("$project", new BasicDBObject("arrayofkeyvalue", new BasicDBObject("$objectToArray", "$$ROOT")));
        BasicDBObject $unwind = new BasicDBObject("$unwind", "$arrayofkeyvalue");
        BasicDBObject $skip = new BasicDBObject("$skip", skip);
        BasicDBObject $limit = new BasicDBObject("$limit", limit);
        BasicDBObject filed = new BasicDBObject("_id", "null");
        filed.append("allkeys", new BasicDBObject("$addToSet", "$arrayofkeyvalue.k"));
        BasicDBObject $group = new BasicDBObject("$group", filed);
        List<BasicDBObject> dbStages = Arrays.asList($project, $skip, $limit, $unwind, $group);
        // System.out.println(dbStages);  發送的聚合函數   獲得所有参數名稱
        AggregateIterable<Document> aggregate = collection.aggregate(dbStages);
        Document document = aggregate.first();
        if (document == null) {
            BasicDBObject existsQuery = new BasicDBObject("$ROOT", new BasicDBObject("$exists", true));
            MongoCursor<Document> existsList = collection.find(existsQuery).limit(100).iterator();
            Set<String> keySet = new HashSet<>();
            while (existsList.hasNext()) {
                Document next = existsList.next();
                Map<String, Object> keyMap = (Document) next.get("$ROOT");
                keySet.addAll(keyMap.keySet());
            }
            return new ArrayList<>(keySet);
        } else {
            return (List<String>) document.get("allkeys");
        }

    }


    /**
     * 如果一個文檔是物件類型  獲得這個屬性的下一級的屬性名的集合
     * 例子: user:{name:"張三",age:12}  傳入user  返回[name,age]
     *
     * @param parameterName 上层参數名  這個参數名可以包含一個或多個.
     *                      注: 参數傳递之前需確認:  1.上层屬性一定是物件類型
     * @return 返回這個屬性内的所有屬性名
     */
    public Set<String> getNextParameterNames(String parameterName) {
        Document condition = new Document(parameterName, new Document("$exists", true));
        Document match = new Document("$match", condition);
        String unwindName = parameterName;
        if (parameterName.contains(".")) {
            unwindName = parameterName.split("\\.")[0];
        }
        Document unwind = new Document("$unwind", "$" + unwindName);
        Document limit = new Document("$limit", 3000);
        Document project = new Document("$project", new Document("list", "$" + parameterName).append("_id", false));
        Document unwind2 = new Document("$unwind", "$list");
        AggregateIterable<Document> aggregate = this.collection.aggregate(Arrays.asList(match, unwind, limit, project, unwind2));
        Set<String> names = new HashSet<>();
        for (Document document : aggregate) {
            Object list = document.get("list");
            if (list instanceof Map) {
                Set<String> documentNames = ((Document) list).keySet();
                names.addAll(documentNames);
            }
        }
        logger.info("解析" + parameterName + "有" + names.size() + "個子屬性");
        return names;
    }


    /**
     * 功能描述:提供屬性名 解析屬性類型
     * 查詢相應的屬性資料  封装成generator物件
     *
     * @return : 解析之後的Model {@see #MongoDefinition}
     * @param: propertyName 屬性名 可以是层級名  比如 name 也可以是info.name
     * @see MongoDefinition
     */

    public MongoDefinition processNameType(String propertyName) {
        MongoCollection<Document> collection = this.collection;
        MongoDefinition result = new MongoDefinition();
        if ("_id".equals(propertyName)) {
            result.setType(2);
            result.setPropertyName("_id");
            return result;
        }
        result.setPropertyName(propertyName);
        MongoCursor<Document> isArray = collection.find(new Document(propertyName, new Document("$type", ARRAY_TYPE))).limit(1).iterator();
        if (isArray.hasNext()) {
            result.setArray(true);
            for (int i : TYPE) {
                MongoCursor<Document> iterator = collection.find(new Document(propertyName, new Document("$type", i))).limit(1).iterator();
                if (iterator.hasNext()) {
                    if (i == 3) {
                        result.setChild(this.produceChildList(propertyName));
                    }
                    //1是double 2是string 3是物件 4是數組 16是int 18 是long
                    result.setType(i);
                    logger.info("解析[" + propertyName + "]是[List][" + Type.typeInfo(result.getType()) + "]");
                    return result;
                }
            }
        } else {
            for (int i : TYPE) {
                MongoCursor<Document> iterator = collection.find(new Document(propertyName, new Document("$type", i))).limit(1).iterator();
                if (iterator.hasNext()) {
                    if (i == 3) {
                        result.setChild(this.produceChildList(propertyName));
                    }
                    //1是double 2是string 3是物件 4是數組 16是int 18 是long
                    //到這裡就是數組了
                    result.setType(i);
                    logger.info("解析[" + propertyName + "]是[" + Type.typeInfo(result.getType()) + "]");
                    return result;
                }
            }
            result.setType(2);
        }
        logger.info("解析[" + propertyName + "]是[" + Type.typeInfo(result.getType()) + "]");
        return result;
    }


    private List<MongoDefinition> produceChildList(String parentName) {
        Set<String> nextParameterNames = this.getNextParameterNames(parentName);
        List<String> strings = new ArrayList<>(nextParameterNames);
        List<String> collect = strings.stream().map(name -> parentName + "." + name).collect(Collectors.toList());
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<List<MongoDefinition>> task = new ForkJoinProcessType(collect);
        return pool.invoke(task);
    }

    private List<String> distinctAndJoin(List<String> a, List<String> b) {
        a.removeAll(b);
        a.addAll(b);
        return a;
    }


    /**
     * 功能描述:解析這個集合的列名  用ForkJoin框架實現
     */
    private void initColNames() {
        long start = System.currentTimeMillis();
        int scan = this.scanCount;
        long count = this.collection.countDocuments();
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<List<String>> task;
        if (count > (long) scan) {
            task = new ForkJoinGetProcessName(0, scan);
        } else {
            task = new ForkJoinGetProcessName(0, (int) count);
        }
        this.colNames = pool.invoke(task);
        logger.info("collection[" + this.collection.getNamespace().getCollectionName() +
                "]初始化列名成功.....     用時: " + (System.currentTimeMillis() - start) + "毫秒");
    }

    private MongoDefinition scanType() {
        MongoDefinition result = new MongoDefinition();
        List<String> colNames = this.colNames;
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<List<MongoDefinition>> task = new ForkJoinProcessType(colNames);
        List<MongoDefinition> invoke = pool.invoke(task);
        return result.setChild(invoke).setPropertyName(this.collection.getNamespace().getCollectionName());
    }

    /**
     * 功能描述:forkJoin多線程框架的實現  通過业務拆分解析類型
     */
    class ForkJoinProcessType extends RecursiveTask<List<MongoDefinition>> {
        List<String> names;
        private final int THRESHOLD = 6;

        ForkJoinProcessType(List<String> names) {
            this.names = names;
        }

        @Override
        protected List<MongoDefinition> compute() {
            if (names.size() <= THRESHOLD) {
                List<MongoDefinition> result = new ArrayList<>();
                for (String name : names) {
                    MongoDefinition childrenDefinition = processNameType(name);
                    result.add(childrenDefinition);
                }
                return result;
            } else {
                int size = names.size();
                int middle = size / 2;
                List<String> leftList = names.subList(0, middle);
                List<String> rightList = names.subList(middle, size);
                ForkJoinProcessType pre = new ForkJoinProcessType(leftList);
                pre.fork();
                ForkJoinProcessType next = new ForkJoinProcessType(rightList);
                next.fork();
                return mergeList(pre.join(), next.join());
            }
        }
    }

    /**
     * 功能描述:forkJoin多線程框架的實現  通過业務拆分獲得屬性名
     */
    class ForkJoinGetProcessName extends RecursiveTask<List<String>> {
        private int begin; //查詢開始位置
        private int end;
        private final int THRESHOLD = 5000;

        ForkJoinGetProcessName(int begin, int end) {
            this.begin = begin;
            this.end = end;
        }

        @Override
        protected List<String> compute() {
            int count = end - begin;
            if (THRESHOLD >= count) {
                return groupAggregation(begin, count);
            } else {
                int middle = (begin + end) / 2;
                ForkJoinGetProcessName pre = new ForkJoinGetProcessName(begin, middle);
                pre.fork();
                ForkJoinGetProcessName next = new ForkJoinGetProcessName(middle + 1, end);
                next.fork();
                return distinctAndJoin(pre.join(), next.join()); //去重合併
            }
        }
    }
    public  <T> List<T> mergeList(List<T> list1, List<T> list2){
        list1.addAll(list2);
        return list1;
    }
}
