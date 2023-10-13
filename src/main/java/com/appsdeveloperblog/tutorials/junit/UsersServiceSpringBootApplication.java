package com.appsdeveloperblog.tutorials.junit;

import com.appsdeveloperblog.tutorials.junit.shared.SpringApplicationContext;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import org.apache.catalina.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@SpringBootApplication
public class UsersServiceSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersServiceSpringBootApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}
}
