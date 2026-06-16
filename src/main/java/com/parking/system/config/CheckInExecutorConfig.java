package com.parking.system.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CheckInExecutorConfig {

    @Bean(name = "checkInTaskExecutor")
    public Executor checkInTaskExecutor() {
        return Executors.newFixedThreadPool(2);
    }
}
