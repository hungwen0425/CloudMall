package com.hungwen.cloudmall.ware.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tesng
 * @createTime: 2020-11-14 15:35
 **/
@Configuration
public class MySeataConfig {

    @Autowired
    DataSourceProperties dataSourceProperties;
    /**
     * 需要將 DataSourceProxy 設置為主資料源，否則事務無法回滾
     * @param dataSourceProperties
     * @return
     */
//    @Bean
//    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
//        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if (StringUtils.hasText(dataSourceProperties.getName())) {
//            dataSource.setPoolName(dataSourceProperties.getName());
//        }
//        return new DataSourceProxy(dataSource);
//    }
}
