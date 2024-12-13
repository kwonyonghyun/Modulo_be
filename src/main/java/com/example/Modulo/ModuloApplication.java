package com.example.Modulo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ModuloApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuloApplication.class, args);
	}

}
