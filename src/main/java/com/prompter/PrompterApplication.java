package com.prompter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PrompterApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrompterApplication.class, args);
	}

}
