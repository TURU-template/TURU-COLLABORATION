package com.turu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.turu")

public class Turux3Application {
	public static void main(String[] args) {
		SpringApplication.run(Turux3Application.class, args);
		System.out.println("[*] Starting: TURU - Aplikasi Monitoring Tidur zZzZ Marcel");
	}
}
