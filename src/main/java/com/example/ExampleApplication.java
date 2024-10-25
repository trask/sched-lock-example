package com.example;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "1m")
public class ExampleApplication {

    private static final Connection connection;

    static {
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:test");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "0/5 * * * * *")
    @SchedulerLock(name = "scheduledTaskName")
    public void helloWorld() {
        System.out.println("Hello world.");
    }

    @Bean
    public LockProvider lockProvider() {
        return new MyLockProvider();
    }

    private static class MyLockProvider implements LockProvider {
        @Override
        public Optional<SimpleLock> lock(LockConfiguration lockConfiguration) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("SELECT * from INVALID");
            } catch (SQLException e) {
                return Optional.empty();
            }
            return Optional.of(new MySimpleLock());
        }
    }

    private static class MySimpleLock implements SimpleLock {
        @Override
        public void unlock() {
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
