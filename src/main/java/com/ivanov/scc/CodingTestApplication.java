package com.ivanov.scc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CodingTestApplication {
	private static final Logger LOG = LoggerFactory.getLogger(CodingTestApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(CodingTestApplication.class, args);
	}
}
