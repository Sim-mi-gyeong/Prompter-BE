package com.prompter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;

@EntityScan("com.prompter")
@ComponentScan(basePackages = "com.prompter")
@SpringBootApplication
public class PrompterApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrompterApiApplication.class, args);
        System.out.println("LocalDateTime.now() = " + LocalDateTime.now());
    }
}
