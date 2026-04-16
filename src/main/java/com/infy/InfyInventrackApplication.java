package com.infy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class InfyInventrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfyInventrackApplication.class, args);
    }
}
