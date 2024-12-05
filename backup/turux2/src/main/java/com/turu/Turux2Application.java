package com.turu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.turu")

public class Turux2Application {
	public static void main(String[] args) {
		SpringApplication.run(Turux2Application.class, args);
		System.out.println("Kok kamu gitu sih bal");
	}

}
