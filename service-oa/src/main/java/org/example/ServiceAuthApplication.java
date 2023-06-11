package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * ClassName: ServiceAuthApplication
 * Package: com.jerry.auth
 * Description:
 *
 * @Author jerry_jy
 * @Create 2023-02-28 22:03
 * @Version 1.0
 */

@SpringBootApplication
//@ComponentScan(basePackages = {"org.example"})
public class ServiceAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthApplication.class, args);
    }
}

