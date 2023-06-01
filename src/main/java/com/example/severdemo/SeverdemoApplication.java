package com.example.severdemo;

import com.example.severdemo.service.TestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SeverdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeverdemoApplication.class, args);
    }

}
