package com.mistatistic.webhookbot;

import com.mistatistic.webhookbot.services.Parser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebhookBotApplication {

	public static void main(String[] args) {

		SpringApplication.run(WebhookBotApplication.class, args);
		//System.out.println(Parser.findByAddress("Марьина Горка"));
	}

}
