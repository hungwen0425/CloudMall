package io.renren.factory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import io.renren.config.MongoCondition;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: gxz gongxuanzhang@foxmail.com
 **/

@Component
@Conditional(MongoCondition.class)
public class MongoDBCollectionFactory {

    private static  final String TABLE_NAME_KEY = "tableName";
    private static final String LIMIT_KEY = "limit";
    private static final String OFFSET_KEY = "offset";

    private static MongoDatabase mongoDatabase;

    // 此處是為了兼容mongo相關内容和關係型資料庫的静態耦合所導致的問题

    @Autowired
    private MongoDatabase database;
    @PostConstruct
    public void initMongoDatabase(){
        mongoDatabase = database;
    }

    /***
     * 通過表名獲得查詢物件
     * @author gxz
     * @date  2020/5/9
     * @param collectionName mongo的集合名(表名)
     * @return 連接查詢物件
     **/
    public MongoCollection<Document> getCollection(String collectionName) {
        return mongoDatabase.getCollection(collectionName);
    }

    /***
     * 獲得當前資料庫的集合名稱
     * 注: mongo相對關係型資料庫较為特殊，查詢表名無法分頁，用stream實現
     * @author gxz
     * @date  2020/5/9
     * @param map 這是查詢条件 和關係型資料庫一致
     * @return 集合名稱
     **/
    public static List<String>  getCollectionNames(Map<String, Object> map) {
        int limit = Integer.valueOf(map.get(LIMIT_KEY).toString());
        int skip = Integer.valueOf(map.get(OFFSET_KEY).toString());
        List<String> names;
        if (map.containsKey(TABLE_NAME_KEY)) {
            names = getCollectionNames(map.get(TABLE_NAME_KEY).toString());
        } else {
            names = getCollectionNames();
        }
        return names.stream().skip(skip).limit(limit).collect(Collectors.toList());
    }
    /***
     * 獲得集合名稱總數(表的數量) 為了适配MyBatisPlus的分頁插件 提供方法
     * @author gxz
     * @date  2020/5/9
     * @param map 這是查詢条件 和關係型資料庫一致
     * @return int
     **/
    public static int getCollectionTotal(Map<String, Object> map) {
        if (map.containsKey(TABLE_NAME_KEY)) {
            return getCollectionNames(map.get(TABLE_NAME_KEY).toString()).size();
        }
        return getCollectionNames().size();

    }


    private static List<String> getCollectionNames() {
        MongoIterable<String> names = mongoDatabase.listCollectionNames();
        List<String> result = new ArrayList<>();
        for (String name : names) {
            result.add(name);
        }
        return result;
    }

    private static List<String> getCollectionNames(String likeName) {
        return getCollectionNames()
                .stream()
                .filter((name) -> name.contains(likeName)).collect(Collectors.toList());
    }
}
