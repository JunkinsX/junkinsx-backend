package com.example.jenkinsx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Bean
    public CommandLineRunner runMigrations(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("ALTER TABLE pipeline_logs ALTER COLUMN command TYPE TEXT;");
                System.out.println("Migrated pipeline_logs.command to TEXT");
            } catch (Exception e) {
                System.out.println("Migration message for pipeline_logs: " + e.getMessage());
            }
            try {
                jdbcTemplate.execute("ALTER TABLE commands_command_list ALTER COLUMN command_list TYPE TEXT;");
                System.out.println("Migrated commands_command_list.command_list to TEXT");
            } catch (Exception e) {
                System.out.println("Migration message for commands_command_list: " + e.getMessage());
            }
        };
    }

}
