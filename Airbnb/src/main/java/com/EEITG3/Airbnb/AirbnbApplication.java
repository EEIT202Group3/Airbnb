package com.EEITG3.Airbnb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AirbnbApplication {

	private static final Logger log = LoggerFactory.getLogger(AirbnbApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(AirbnbApplication.class, args);
		log.info("app start");
	}

}
