package com.example.just;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableCaching
public class JustApplication {

	@Bean
	public BCryptPasswordEncoder encoder(){
		return new BCryptPasswordEncoder();
	}
	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}
	public static void main(String[] args) {
		SpringApplication.run(JustApplication.class, args);
	}

}