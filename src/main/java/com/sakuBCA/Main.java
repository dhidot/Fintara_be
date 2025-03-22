package com.sakuBCA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.sakuBCA")
@EntityScan(basePackages = "com.sakuBCA.models")
@EnableJpaRepositories(basePackages = "com.sakuBCA.repositories")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
