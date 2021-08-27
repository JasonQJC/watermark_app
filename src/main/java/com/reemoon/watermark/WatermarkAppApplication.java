package com.reemoon.watermark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WatermarkAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatermarkAppApplication.class, args);
	}

}
