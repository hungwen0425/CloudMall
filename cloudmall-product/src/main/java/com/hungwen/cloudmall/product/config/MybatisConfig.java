package com.hungwen.cloudmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement // 開啟 Transaction 註解
@Configuration
@MapperScan("com.hungwen.cloudmall.product.dao")
public class MybatisConfig {
    //引入分頁插件
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 設定請求的頁面大於最大頁後操作， true 調回到首頁，false 繼續請求  默認 false
        paginationInterceptor.setOverflow(true);
        //設定最大單頁限制數量，默認 500 條，-1 不受限制
        paginationInterceptor.setLimit(1000);
        // 開啟 count 的 join 優化,只針對部分 left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }
}
