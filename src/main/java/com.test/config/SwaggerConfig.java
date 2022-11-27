package com.test.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

/**
 * The type Swagger config.
 */
@Configuration
public class SwaggerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerConfig.class);

    /**
     * Docket docket.
     * @param environment the environment
     * @return the docket
     */
    @Bean
    public Docket docket(Environment environment) {
        // 配置swagger要使用的环境
        Profiles profiles = Profiles.of("dev", "default");
        // environment.acceptsProfiles判断自己是否在自己设定的环境中
        boolean flag = environment.acceptsProfiles(profiles);
        LOGGER.info("config swagger enabled:{}", flag);
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("标准服务组")
                // eanble决定了是否启动swagger
                .enable(flag)
                .select()
                /*apis  指定我们需要基于什么包扫描
                 * RequestHandlerSelectors扫描接口的方式
                 * basePackage指定扫描包
                 * any-扫描全部
                 * none-不扫描
                 * withclassannotation 扫描类的注解（里面必须放注解的反射对象）
                 */
                .apis(RequestHandlerSelectors.basePackage("com.test.controller"))
                /*paths 过滤哪里什么路径*/
                // .paths(PathSelectors.ant("/hyc/**"))
                .build()
                .apiInfo(this.apiInfo());
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("dream-life", "dream.com", "903936451@qq.com");
        return new ApiInfo(
                "标准服务集成Swagger3",
                "标准服务集成Swagger3接口文档",
                "1.0",
                "hyc.com",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList());
    }
}
