package io.appactive.demo.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
// @ComponentScan(basePackages = "io.appactive.demo")
// @Configuration
// @EnableAutoConfiguration
public class FrontendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrontendApplication.class, args);
    }
}
