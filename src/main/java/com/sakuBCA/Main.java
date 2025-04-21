package com.sakuBCA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@ComponentScan(basePackages = "com.sakuBCA")
@EntityScan(basePackages = "com.sakuBCA.models")
@EnableJpaRepositories(basePackages = "com.sakuBCA.repositories")
@EnableScheduling
public class Main {
    public static void main(String[] args) {
        // Load .env ke System Environment
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // biar tetap jalan kalau gak ada file .env
                .load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(Main.class, args);
    }
}