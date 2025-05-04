package com.email_bulk.email_campign;

import com.email_bulk.email_list.EmailListApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CampaignApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailListApplication.class, args);
	}

}
