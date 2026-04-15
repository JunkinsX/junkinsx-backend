package com.example.jenkinsx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class JenkinsXApplication {

    public static void main(String[] args) {
        SpringApplication.run(JenkinsXApplication.class, args);

    }

}
