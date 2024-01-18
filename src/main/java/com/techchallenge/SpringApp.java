package com.techchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;


@EnableKafka
@SpringBootApplication
@EnableFeignClients
public class SpringApp {

	public static void main(String[] args) {
		SpringApplication.run(SpringApp.class, args);
	}

}
