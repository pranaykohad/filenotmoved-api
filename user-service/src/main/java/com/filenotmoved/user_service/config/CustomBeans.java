package com.filenotmoved.user_service.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomBeans {
	
	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
