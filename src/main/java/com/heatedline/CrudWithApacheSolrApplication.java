package com.heatedline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CrudWithApacheSolrApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudWithApacheSolrApplication.class, args);
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer () {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				System.out.println("cors configure");
				registry.addMapping("/**").allowedOrigins("http://localhost");
			}
		};
	}

}
