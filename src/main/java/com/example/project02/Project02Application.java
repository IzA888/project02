package com.example.project02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.project02.repository")
public class Project02Application {

	public static void main(String[] args) {
		SpringApplication.run(Project02Application.class, args);
	}

}
