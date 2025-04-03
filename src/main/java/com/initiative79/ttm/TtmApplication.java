package com.initiative79.ttm;

import com.initiative79.ttm.services.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TtmApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(TtmApplication.class, args);
	}

}
