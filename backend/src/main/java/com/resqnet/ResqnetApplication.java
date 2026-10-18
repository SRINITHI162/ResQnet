package com.resqnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ResqnetApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResqnetApplication.class, args);
    }
}
