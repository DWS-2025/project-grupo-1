package es.codeurjc.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Main entry point for the Spring Boot web application.
 * <p>
 * This class is annotated with {@link org.springframework.boot.autoconfigure.SpringBootApplication}
 * to enable auto-configuration, component scanning, and configuration properties support.
 * </p>
 * <p>
 * The {@link org.springframework.data.web.config.EnableSpringDataWebSupport} annotation is used
 * to enable support for Spring Data web features, such as pagination and sorting, with
 * page serialization mode set to {@code VIA_DTO}.
 * </p>
 * 
 * @author Grupo 1
 */
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO) // Spring Data Web support

public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

}
