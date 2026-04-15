package com.example.jenkinsx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EntityScan(basePackages = "com.example.jenkinsx.entity")
@EnableJpaRepositories(basePackages = "com.example.jenkinsx.repository")
public class JenkinsXApplication {

    public static void main(String[] args) {
        SpringApplication.run(JenkinsXApplication.class, args);

    }

}
