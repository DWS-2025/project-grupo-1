package es.codeurjc.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class WebApplication {

	public static void main(String[] args) {
		Manager manager = new Manager();
		manager.init();
		SpringApplication.run(WebApplication.class, args);
	}

}
