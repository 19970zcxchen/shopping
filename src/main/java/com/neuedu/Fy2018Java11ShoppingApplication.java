package com.neuedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Fy2018Java11ShoppingApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Fy2018Java11ShoppingApplication.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(Fy2018Java11ShoppingApplication.class, args);
    }

}

