package com.test;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;


/**
 * The type Application. swagger访问地址: http://localhost:8080/swagger-ui/index.html
 */
@EnableCaching
@EnableScheduling
@EnableAsync
// 不配置数据源可启动
//@SpringBootApplication(scanBasePackages = {"com.test"}, exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@SpringBootApplication(scanBasePackages = {"com.test"})
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    // private static ConfigurableApplicationContext configurableApplicationContext;

    /**
     * The entry point of application.
     * @param args the input arguments
     */
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(Application.class, args);
    }
}
