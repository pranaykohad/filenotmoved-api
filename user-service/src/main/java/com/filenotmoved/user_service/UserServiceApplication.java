package com.filenotmoved.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import com.filenotmoved.user_service.entity.Role;
import com.filenotmoved.user_service.repository.RoleRepository;
import java.util.Arrays;

@SpringBootApplication
@EntityScan("com.filenotmoved.user_service.entity")
@EnableJpaRepositories("com.filenotmoved.user_service.repository")
public class UserServiceApplication {

	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@PostConstruct
	public void initRoles() {
		if (roleRepository.count() == 0) {
			roleRepository.saveAll(Arrays.asList(
					new Role("USER"),
					new Role("ADMIN"),
					new Role("GOVT")
			));
		}
	}

}
