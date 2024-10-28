package com.example.aniwhere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AniwhereApplication {

	public static void main(String[] args) {
		SpringApplication.run(AniwhereApplication.class, args);
	}

}
