package com.github.alinalobkova.reportbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReportbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportbotApplication.class, args);
        System.out.println("start");
    }

}
