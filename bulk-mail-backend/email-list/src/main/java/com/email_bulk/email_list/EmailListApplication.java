package com.email_bulk.email_list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EmailListApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailListApplication.class, args);
	}

}
