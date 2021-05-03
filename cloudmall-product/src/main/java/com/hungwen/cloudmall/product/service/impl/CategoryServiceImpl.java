package com.hungwen.cloudmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hungwen.cloudmall.product.service.CategoryBrandRelationService;
import com.hungwen.cloudmall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.Query;

import com.hungwen.cloudmall.product.dao.CategoryDao;
import com.hungwen.cloudmall.product.entity.CategoryEntity;
import com.hungwen.cloudmall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    StringRedisTemplate springRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1. 查出所有分類
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2. 組裝成父子的樹型結構
        //2.1 找到所有一級分類
        List<CategoryEntity> level1Menu = entities.stream().filter(
                categoryEntities -> categoryEntities.getParentCid() == 0
        ).map((menu)->{
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2)->{
            //選單的排序
            return (menu1.getSort() == null ? 0:menu1.getSort()) - (menu2.getSort() == null ? 0:menu2.getSort());
        }).collect(Collectors.toList());

        return level1Menu;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1. 檢查當前刪除的選單，是否被別的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    // [2, 25, 225]
    @Override
    public Long[] findCatelogPath(long catelogId) {
        List<Long> paths = Lists.newArrayList();
        List<Long> parentPath = findParentPath(catelogId, paths);
        // 因為收集的鎮列為 [225, 25, 2]，所以要用 Collections.reverse(paths) 轉為 [2, 25, 225]
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 級聯更新所有關聯的資料
     * @CacheEvict: 失效模式，選單一但被修改後，即刪除 cache 中的資料
     * 1. 同時進行多種 cache 操作   @Caching
     * 2. 指定刪除某個分區下的所有資料
     * 3. 存儲同一類型的資料，都可以指定成同一個分區，分區名默認就是 cache 的前綴＊
     *
     * @Caching(evict = {　@CacheEvict(value = "category", key = "'getLevel1Categorys'"),
     * @CacheEvict(value = "category", key = "'getCatelogJson'")
     *
     */
    @CacheEvict(value="category", allEntries=true)       //刪除某個分區下的所有資料
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    //225 -> 25 -> 2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1. 收集當前節點 id
        paths.add(catelogId);
        //先找出當前節點
        CategoryEntity category = this.getById(catelogId);
        //如果該節點的父節點 id 不等於於 0，找其父節點
        if (category.getParentCid() != 0) {
            findParentPath(category.getParentCid(), paths);
        }
        return paths;
    }

    /**
     *
     * @param root 當前選單
     * @param all 當前選單的子選單
     * @return
     */
    //遞歸查找所有選單的子選單
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all){

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1. 找到子選單
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2)-> {
            //2. 選單的排序
            return (menu1.getSort() == null ? 0:menu1.getSort()) - (menu2.getSort() == null ? 0:menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

    /**
     * 查出所有的商品選單一級分類
     * @return
     */
    @Cacheable(value="category", key="#root.methodName", sync=true) // sync=true 加鎖，即可解決緩存擊穿
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("getLevel1Categorys........");
        long l = System.currentTimeMillis();
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        System.out.println("消耗時間："+ (System.currentTimeMillis() - l));
        return categoryEntities;
    }

    @Cacheable(value="category", key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查詢了資料庫");
        //將資料庫的多次查詢變為一次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);
        // 1.查出所有一級分類
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //封裝資料
        Map<String, List<Catelog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(
        k -> k.getCatId().toString(),
        v -> {
            // 2. 每一個的一級分類,查到這個一級分類的二級分類
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            // 封裝資料
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());
                    // 3. 找當前二級分類的三級分類封裝成 vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                            // 封裝資料
                            Catelog2Vo.Catelog3Vo category3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return category3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        return parentCid;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        ValueOperations<String, String> ops = springRedisTemplate.opsForValue();
        String catalogJson = ops.get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            System.out.println("緩存不命中...查詢資料庫...");
            // 緩存中沒有資料，查詢資料庫
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedissonLock();
            return catalogJsonFromDb;
        }
        System.out.println("緩存命中...直接返回...");
        // 轉為指定的物件
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson,new TypeReference<Map<String, List<Catelog2Vo>>>(){});
        return result;
    }

    /**
     * 緩存裡的資料如何和資料庫的資料保持一致？？
     * 緩存資料一致性
     * 1)、雙寫模式
     * 2)、失效模式
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        // 1. 占分布式鎖，鎖的粒度，越細越快:
        // 鎖的粒度，具體緩存的是某個資料：11號商品 - product-11-lock
        RLock catalogJsonLock = redissonClient.getLock("catalogJson-lock");
        catalogJsonLock.lock();

        Map<String, List<Catelog2Vo>> dataFromDb = null;
        try {
            dataFromDb = getCatelogJsonFromDb();
        } finally {
            catalogJsonLock.unlock();
        }
        return dataFromDb;
    }

    /**
     * 邏輯是
     * 1. 首先查詢 Redis 緩存中是否有分類資料，有則返回，否則繼續執行
     * 2. 根據一級分類，找到對應的二級分類
     * 3. 將得到的二級分類，封裝到 Catelog2Vo 中
     * 4. 根據二級分類，得到對應的三級分類
     * 5. 將三級分類封裝到 Catalog3List
     * 6. 將查詢結果放入到 Redis 中
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDb() {
        System.out.println("查詢資料庫......");
        // 1. 先從 Redis 緩存中查詢，如果有資料，則返回查詢結果
        String catelogJson = springRedisTemplate.opsForValue().get("catelogJson");
        if(!StringUtils.isEmpty(catelogJson)){
            log.warn("從緩存中查詢資料");
            return JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
        }
        log.warn("查詢資料庫");
        // 一次性查詢出所有的分類資料，減少對於資料庫的訪問次數，後面的資料操作並不是到資料庫中查詢，而是直接從這個集合中查詢，
        // 由於分類資料的量並不大，所以這種方式是可行的
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(null);
        // 查出所有一級分類
        List<CategoryEntity> level1Categories = getParent_cid(categoryEntities,0L);

        Map<String, List<Catelog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), level1 -> {
            // 2. 根據一級分類的 id 查找到對應的二級分類
            List<CategoryEntity> level2Categories = getParent_cid(categoryEntities,level1.getCatId());
            // 3. 根據二級分類，查找到對應的三級分類
            List<Catelog2Vo> catelog2Vos =null;
            if(null != level2Categories || level2Categories.size() > 0){
                catelog2Vos = level2Categories.stream().map(level2 -> {
                    // 得到對應的三級分類
                    List<CategoryEntity> level3Categories = getParent_cid(categoryEntities,level2.getCatId());
                    // 封裝到Catalog3List
                    List<Catelog2Vo.Catelog3Vo> catalog3Lists = null;
                    if (null != level3Categories) {
                        catalog3Lists = level3Categories.stream().map(level3 -> {
                            Catelog2Vo.Catelog3Vo catalog3List = new Catelog2Vo.Catelog3Vo(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return catalog3List;
                        }).collect(Collectors.toList());
                    }
                    return new Catelog2Vo(level1.getCatId().toString(), catalog3Lists, level2.getCatId().toString(), level2.getName());
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        // 6. 將查詢結果放入到 Redis 中
        springRedisTemplate.opsForValue().set("catelogJson",JSON.toJSONString(parent_cid),1, TimeUnit.DAYS);
        return parent_cid;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList ,Long parentCid) {
        List<CategoryEntity> categoryEntities = selectList.stream().filter(item ->
                item.getParentCid().equals(parentCid)
        ).collect(Collectors.toList());
        return categoryEntities;
    }

//    5.@Override
//    public Map<String, List<Catelog2Vo>> getCatalogJson() {
//        // 先從緩存中查詢分類資料，如果沒有再從資料庫中查詢，並且分類資料是以 JSON 的形式存放到 Reids 中的
//        String catelogJson = springRedisTemplate.opsForValue().get("catelogJson");
//        //1. 空結果緩存：解決緩存穿透
//        //2. 設定過期時間 (加隨機值)：解決緩存雪崩
//        //3. 加鎖：解決緩存擊穿（使用分布式鎖）
//        if(StringUtils.isEmpty(catelogJson)){
//            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatelogJsonFromDbWithRedisLock();
//            return catelogJsonFromDb;
//        }
//        Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
//        });
//        log.warn("緩存命中");
//        return  stringListMap;
//    }
//
//    /**
//     * 使用分布式鎖來實現多個服務共享同一緩存中的資料
//     *  1. 設定讀寫鎖，失敗則表明其他線程先於該線程獲取到了鎖，則執行自旋，成功則表明獲取到了鎖
//     *  2. 獲取鎖成功，查詢資料庫，查詢分類資料
//     *  3. 釋放鎖
//     * @return
//     */
//    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedisLock() {
//        String uuid= UUID.randomUUID().toString();
//        // 設定 Redis 分布式鎖，成功則返回 true，否則返回 false，該操作是原子性的
//        Boolean lock = springRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
//        if(lock==null || !lock){
//            // 加鎖失敗，重試
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//
//            }
//            log.warn("獲取鎖失敗，重新查詢...");
//            return getCatelogJsonFromDbWithRedisLock();
//        }else{
//            // 加鎖成功
//            log.warn("獲取鎖成功:");
//            Map<String, List<Catelog2Vo>> catelogJsonFromDb = null;
//            try {
//                // 從資料庫中查詢分類資料
//                catelogJsonFromDb = getCatelogJsonFromDb();
//            } finally {
//                // 確保一定會釋放鎖
//                String script="if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
//                springRedisTemplate.execute(new DefaultRedisScript(script,Long.class), Arrays.asList("lock"), uuid);
//                log.warn("釋放鎖成功:");
//            }
//            return catelogJsonFromDb;
//        }
//    }
//
//    /**
//     * 邏輯是
//     * 1. 首先查詢 Redis 緩存中是否有分類資料，有則返回，否則繼續執行
//     * 2. 根據一級分類，找到對應的二級分類
//     * 3. 將得到的二級分類，封裝到 Catelog2Vo 中
//     * 4. 根據二級分類，得到對應的三級分類
//     * 5. 將三級分類封裝到 Catalog3List
//     * 6. 將查詢結果放入到 Redis 中
//     * @return
//     */
//    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDb() {
//        System.out.println("查詢資料庫......");
//        // 1. 先從 Redis 緩存中查詢，如果有資料，則返回查詢結果
//        String catelogJson = springRedisTemplate.opsForValue().get("catelogJson");
//        if(!StringUtils.isEmpty(catelogJson)){
//            log.warn("從緩存中查詢資料");
//            return JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
//        }
//        log.warn("查詢資料庫");
//        // 一次性查詢出所有的分類資料，減少對於資料庫的訪問次數，後面的資料操作並不是到資料庫中查詢，而是直接從這個集合中查詢，
//        // 由於分類資料的量並不大，所以這種方式是可行的
//        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(null);
//        // 查出所有一級分類
//        List<CategoryEntity> level1Categories = getParent_cid(categoryEntities,0L);
//
//        Map<String, List<Catelog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), level1 -> {
//            // 2. 根據一級分類的 id 查找到對應的二級分類
//            List<CategoryEntity> level2Categories = getParent_cid(categoryEntities,level1.getCatId());
//            // 3. 根據二級分類，查找到對應的三級分類
//            List<Catelog2Vo> catelog2Vos =null;
//            if(null != level2Categories || level2Categories.size() > 0){
//                catelog2Vos = level2Categories.stream().map(level2 -> {
//                    // 得到對應的三級分類
//                    List<CategoryEntity> level3Categories = getParent_cid(categoryEntities,level2.getCatId());
//                    // 封裝到Catalog3List
//                    List<Catelog2Vo.Catelog3Vo> catalog3Lists = null;
//                    if (null != level3Categories) {
//                        catalog3Lists = level3Categories.stream().map(level3 -> {
//                            Catelog2Vo.Catelog3Vo catalog3List = new Catelog2Vo.Catelog3Vo(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
//                            return catalog3List;
//                        }).collect(Collectors.toList());
//                    }
//                    return new Catelog2Vo(level1.getCatId().toString(), catalog3Lists, level2.getCatId().toString(), level2.getName());
//                }).collect(Collectors.toList());
//            }
//            return catelog2Vos;
//        }));
//        // 6. 將查詢結果放入到 Redis 中
//        springRedisTemplate.opsForValue().set("catelogJson",JSON.toJSONString(parent_cid),1, TimeUnit.DAYS);
//        return parent_cid;
//    }
//
//    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList ,Long parentCid) {
//        List<CategoryEntity> categoryEntities = selectList.stream().filter(item ->
//                item.getParentCid().equals(parentCid)
//        ).collect(Collectors.toList());
//        return categoryEntities;
//    }5.

//    4./**
//     * redis + 鎖
//     * 1. 空結果 cache ，解決 cache 穿透
//     * 2. 設定過期時間 (加隨機值)，解決 cache 雪崩
//     * 3. 加鎖，解決 cache 擊穿
//     */
//    @Override
//    public Map<String, List<Catelog2Vo>> getCatalogJson() {
//        //先從緩存中查詢分類資料，如果沒有再從資料庫中查詢，並且分類資料是以JSON的形式存放到Reids中的
//        String catelogJson = springRedisTemplate.opsForValue().get("catelogJson");
//        //1. 空結果緩存：解決緩存穿透
//        //2. 設定過期時間(加隨機值)：解決緩存雪崩
//        //3. 加鎖：解決緩存擊穿
//        //使用 DCL（雙端檢鎖機制）來完成對於資料庫的訪問
//        if(StringUtils.isEmpty(catelogJson)){
//            synchronized (this){
//                String catelogJson2 = springRedisTemplate.opsForValue().get("catelogJson");
//                if (StringUtils.isEmpty(catelogJson2)) {
//                    //如果緩存中沒有，則查詢資料庫，並將查詢結果放入到緩存中
//                    Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatalogJsonFromDb();
//                    springRedisTemplate.opsForValue().set("catelogJson",JSON.toJSONString(catelogJsonFromDb),1, TimeUnit.DAYS);
//                    //log.info("緩存未命中，該線程是：{}",Thread.currentThread().getId()+" "+Thread.currentThread().getName());
//                    System.out.println("緩存未命中，該線程是："+Thread.currentThread().getName());
//                    return catelogJsonFromDb;
//                }
//            }
//        }
//
//        Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
//        });
//        //log.info("緩存命中，該線程是：{}",Thread.currentThread().getId()+" "+Thread.currentThread().getName());
//        System.out.println("緩存命中，該線程是："+" "+Thread.currentThread().getName());
//
//        return  stringListMap;
//    }
//
//    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
//        // 將資料庫的多次查詢變為一次
//        List<CategoryEntity> selectList = this.baseMapper.selectList(null);
//        // 1. 查出所有分類
//        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
//        // 封裝資料
//        Map<String, List<Catelog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(
//            k -> k.getCatId().toString(),
//            v -> {
//                // 2. 根據每一個的一級分類，查詢其二級分類
//                List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
//                // 封裝資料
//                List<Catelog2Vo> catelog2Vos = null;
//                if (categoryEntities != null) {
//                    catelog2Vos = categoryEntities.stream().map(item2 -> {
//                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(),null,
//                                item2.getCatId().toString(), item2.getName().toString());
//                        // 3. 找當前二級分類的三級分類封裝成 vo
//                        List<CategoryEntity> level3Catelog = getParent_cid(selectList, item2.getCatId());
//                        if (level3Catelog != null) {
//                            List<Catelog2Vo.Catelog3Vo> category3Vos = level3Catelog.stream().map(item3 -> {
//                                // 封裝資料
//                                Catelog2Vo.Catelog3Vo category3Vo = new Catelog2Vo.Catelog3Vo(item3.getCatId().toString(),
//                                        item3.getCatId().toString(), item3.getName());
//                                return category3Vo;
//                            }).collect(Collectors.toList());
//                            catelog2Vo.setCatalog3List(category3Vos);
//                        }
//                        return catelog2Vo;
//                    }).collect(Collectors.toList());
//                }
//                return catelog2Vos;
//            }));
//        springRedisTemplate.opsForValue().set("catelogJson",JSON.toJSONString(parentCid),1, TimeUnit.DAYS);
//        return parentCid;
//    }
//
//    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList ,Long parentCid) {
//        List<CategoryEntity> categoryEntities = selectList.stream().filter(item ->
//                item.getParentCid().equals(parentCid)
//        ).collect(Collectors.toList());
//        return categoryEntities;
//    }4.

//    3.@Override
//    public Map<String, List<Catelog2Vo>> getCatalogJson() {
//        // 先從緩存中查詢分類資料，如果沒有再從資料庫中查詢，並且分類資料是以 JSON 的形式存放到 reids 中的
//        String catelogJson = springRedisTemplate.opsForValue().get("catelogJson");
//        // 如果緩存中沒有，則查詢資料庫
//        if(StringUtils.isEmpty(catelogJson)){
//            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatalogJsonFromDb();
//            // 將查詢結果 catelogJsonFromDb 物件轉為 JSON 放入緩存
//            springRedisTemplate.opsForValue().set("catelogJson", JSON.toJSONString(catelogJsonFromDb));
//            return catelogJsonFromDb;
//        }
//        // 將 JSON 轉為指定的物件
//        Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
//        });
//        return  stringListMap;
//    }
//
//    // 從資料庫查詢並封裝分類資料
//    //@Override
//    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
//        // 將資料庫的多次查詢變為一次
//        List<CategoryEntity> selectList = this.baseMapper.selectList(null);
//        // 1. 查出所有分類
//        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
//        // 封裝資料
//        Map<String, List<Catelog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(
//                k -> k.getCatId().toString(),
//                v -> {
//                    // 2. 根據每一個的一級分類，查詢其二級分類
//                    List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
//                    // 封裝資料
//                    List<Catelog2Vo> catelog2Vos = null;
//                    if (categoryEntities != null) {
//                        catelog2Vos = categoryEntities.stream().map(item2 -> {
//                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(),null,
//                                    item2.getCatId().toString(), item2.getName().toString());
//                            // 3. 找當前二級分類的三級分類封裝成 vo
//                            List<CategoryEntity> level3Catelog = getParent_cid(selectList, item2.getCatId());
//                            if (level3Catelog != null) {
//                                List<Catelog2Vo.Catelog3Vo> category3Vos = level3Catelog.stream().map(item3 -> {
//                                    // 封裝資料
//                                    Catelog2Vo.Catelog3Vo category3Vo = new Catelog2Vo.Catelog3Vo(item3.getCatId().toString(),
//                                            item3.getCatId().toString(), item3.getName());
//                                    return category3Vo;
//                                }).collect(Collectors.toList());
//                                catelog2Vo.setCatalog3List(category3Vos);
//                            }
//                            return catelog2Vo;
//                        }).collect(Collectors.toList());
//                    }
//                    return catelog2Vos;
//                }));
//        return parentCid;
//    }
//
//    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList ,Long parentCid) {
//        List<CategoryEntity> categoryEntities = selectList.stream().filter(item ->
//                item.getParentCid().equals(parentCid)
//        ).collect(Collectors.toList());
//        return categoryEntities;
//    }3.

//    2.@Override
//    public Map<String, List<Catelog2Vo>> getCatalogJson() {
//        // 將資料庫的多次查詢變為一次
//        List<CategoryEntity> selectList = this.baseMapper.selectList(null);
//        // 1. 查出所有分類
//        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
//        // 封裝資料
//        Map<String, List<Catelog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(
//                k -> k.getCatId().toString(),
//                v -> {
//                    // 2. 根據每一個的一級分類，查詢其二級分類
//                    List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
//                    // 封裝資料
//                    List<Catelog2Vo> catelog2Vos = null;
//                    if (categoryEntities != null) {
//                        catelog2Vos = categoryEntities.stream().map(item2 -> {
//                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(),null,
//                                    item2.getCatId().toString(), item2.getName().toString());
//                            // 3. 找當前二級分類的三級分類封裝成 vo
//                            List<CategoryEntity> level3Catelog = getParent_cid(selectList, item2.getCatId());
//                            if (level3Catelog != null) {
//                                List<Catelog2Vo.Catelog3Vo> category3Vos = level3Catelog.stream().map(item3 -> {
//                                    // 封裝資料
//                                    Catelog2Vo.Catelog3Vo category3Vo = new Catelog2Vo.Catelog3Vo(item3.getCatId().toString(),
//                                            item3.getCatId().toString(), item3.getName());
//                                    return category3Vo;
//                                }).collect(Collectors.toList());
//                                catelog2Vo.setCatalog3List(category3Vos);
//                            }
//                            return catelog2Vo;
//                        }).collect(Collectors.toList());
//                    }
//                    return catelog2Vos;
//                }));
//        return parentCid;
//    }
//
//    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList ,Long parentCid) {
//        List<CategoryEntity> categoryEntities = selectList.stream().filter(item ->
//                item.getParentCid().equals(parentCid)
//        ).collect(Collectors.toList());
//        return categoryEntities;
//    }2.

//    1.@Override
//    public Map<String, List<Catelog2Vo>> getCatalogJson() {
//        log.debug("查詢了資料庫......");
//        // 1. 查詢商品選單一級分類
//        List<CategoryEntity> level1Categoers = getLevel1Categorys();
//        // 封裝資料
//        Map<String, List<Catelog2Vo>> parent_cid = level1Categoers.stream().collect(Collectors.toMap(
//        k -> String.valueOf(k.getCatId()),
//        v -> {
//            // 2. 根據每一個的一級分類，查詢其二級分類
//            List<CategoryEntity> categoryEntitys = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
//            // 封裝資料
//            List<Catelog2Vo> catelog2Vos = null;
//            if(categoryEntitys != null){
//                catelog2Vos = categoryEntitys.stream().map(item ->{
//                    Catelog2Vo catelog2Vo = new Catelog2Vo(String.valueOf(v.getCatId()), null, item.getCatId().toString(), item.getName());
//                    // 3. 依據現在的二級分類，找出所有的三級分類，並且封裝
//                    List<CategoryEntity> level3Categorys = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
//                    if(level3Categorys != null){
//                        List<Catelog2Vo.Catelog3Vo> category3Vos = level3Categorys.stream().map(item3 -> {
//                        // 封裝資料
//                        Catelog2Vo.Catelog3Vo category3Vo = new Catelog2Vo.Catelog3Vo(item3.getCatId().toString(),
//                                item3.getCatId().toString(), item3.getName());
//                        return category3Vo;
//                    }).collect(Collectors.toList());
//                    }
//                    return  catelog2Vo;
//                }).collect(Collectors.toList());
//            }
//            return catelog2Vos;
//        }));
//        return parent_cid;
//    }1.
}
