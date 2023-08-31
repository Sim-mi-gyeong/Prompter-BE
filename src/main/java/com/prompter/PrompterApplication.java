package com.prompter;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrompterApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrompterApplication.class, args);
		WebDriverManager.chromedriver().setup();
	}

}
