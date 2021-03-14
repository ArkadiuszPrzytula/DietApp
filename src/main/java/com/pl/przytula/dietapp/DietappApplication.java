package com.pl.przytula.dietapp;

import com.pl.przytula.dietapp.config.AppAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppAuthProperties.class)
@SpringBootApplication
public class DietappApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietappApplication.class, args);
    }

}
