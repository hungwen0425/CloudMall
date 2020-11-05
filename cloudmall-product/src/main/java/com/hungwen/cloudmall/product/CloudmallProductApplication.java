package com.hungwen.cloudmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/*
 * 1. 整合 Mybetis-Plus
 *   1.1 導入 dependency
 *       <dependency>
 *           <groupId>com.baomidou</groupId>
 *           <artifactId>mybatis-plus</artifactId>
 *           <version>3.3.1</version>
 *       </dependency>
 *   1.2 設定
 *       1.2.1 設定DataSource
 *             1.2.1.1 導入資訊庫的驅動
 *             1.2.1.2 在 application.yml 設定 DataSourcevu 相關設定
 *       1.2.2 設定 Mybetis-Plus
 *             1.2.2.1 使用 @MapperScan
 *             1.2.2.2 告訴 Mybetis-Plus sql 映射檔案位置
 *  2. 邏輯刪除
 *    2.1 設定全局的邏輯刪除規則 (省略)
 *    2.2 設定邏輯刪除的祖店 (省略)
 *    2.3 給 Bean 加上邏輯刪除註解 @TableLogic
 *  3. 後端校驗，使用JSR303
 *    3.1 給 bean 新增校驗註解: javax.validation.constraints，並定義自己的 message 提示
 *    3.2 必須在 controller 開啟校驗註解 @Valid
 *    3.3 在 controller file 中，校驗的 bean 後面緊跟一個 BindingResult，就可以取得校驗結果
 *    3.4 分組校驗（多場景的複雜校驗）
 *      3.4.1 @NotBlank(message = "品牌名稱必須提交", groups = {AddGroup.class, UpdateGroup.class})
 *            給校驗註解標註甚麼情況下要進行校驗
 *            @Validated({AddGroup.class})
 *            默認沒有指定分組的校驗註解，例如 @NotBlank，在分組校驗的情況下不生效，只會在 @Validated 生效
 *    3.5 自定義校驗
 *      3.5.1 編寫一個自定義的校驗註解 @interface ListValue.class
 *      3.5.2 編寫一個自定義的校驗器 ConstraintValidator
 *      3.5.3 關聯自定義的校驗器與自定義的校驗註解
 *            @Documented
 *            @Constraint(validatedBy = { ListValueConstraintValidator.class [可以指定多個不同的校驗器，適配不同類型的校驗] })
 *            @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
 *            @Retention(RUNTIME)
 *  4. 統一的異常處理: 使用 springMVC 的 @ControllerAdvice
 *    4.1 使用 @ExceptionHandler 註解方法可以處理的異常
 *  5. 模板引勤
 *    5.1 thymeleaf-starter 關閉 cache
 *    5.2 靜態資源都放在 static 資料夾下就可以按照路徑直接訪問
 *    5.3 頁面 (.html) 都放在 template 資料夾下，直接訪問
 *        SpringBoot 訪問項目的時候，默認會找 index.html
 *    5.4 頁面修改不重啟服務器，時時更新
 *       5.4.1 引入 dev-tools
 *       5.4.2 修改完頁面，按 Ctrl + Shift + F9，重新自動編譯頁面
 *  6. 整合 redis
 *    6.1 引入 spring-boot-data-redis-starter
 *    6.2 在 application.yml 設定 redis 的 host, port
 *    6.3 使用 SpringBoot 自動設定好的 SpringRedisTemplate 來操作 redis
 *  7. 整合 SpringCache 簡化緩存開發
 *    7.1 引入依賴 spring-boot-starter-cache、spring-boot-starter-data-redis
 *    7.2 寫設定檔案
 *       7.2.1 Spring 自動幫我們設定了哪些
 *             CacheAutoConfiguration -> CacheProperties -> CacheConfigurations -> RedisCacheConfiguration
 *          -> RedisCacheManager 緩存管理器
 *       7.2.2 設定使用 redis 作為緩存
 *             spring.cache.type=redis
 *    7.3 測試使用緩存
 *        @Cacheable: Trigger cache population. 觸發將資料保存到 cache 的操作
 *        @CacheEvict: Trigger cache eviction. 觸發將資料從 cache 刪除的操作
 *        @CCachePut: Update the cache without interfering with the method execution. 更新 cache
 *        @Caching: Regroups multiple cache operations to be applied on a method. 組合以上多個操作
 *        @CacheConfig: Share some common cache-related setting at class-level. 在類級別共享 cache 的設定
 *       7.3.1 開啟緩存功能 @EnableCaching
 *       7.3.2 只需要使用註解就能完成緩存操作
 *
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.hungwen.cloudmall.product.feign")
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.hungwen.cloudmall.product.dao") // 參考 1.2.2.1
public class CloudmallProductApplication {

    public static void main(String[] args) {

        SpringApplication.run(CloudmallProductApplication.class, args);
    }

}
