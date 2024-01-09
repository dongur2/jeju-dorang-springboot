package com.donguri.jejudorang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JejuDorangApplication {

	public static void main(String[] args) {
		SpringApplication.run(JejuDorangApplication.class, args);
	}

}
