package com.interviewbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InterviewBridgeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewBridgeBackendApplication.class, args);
	}

}
