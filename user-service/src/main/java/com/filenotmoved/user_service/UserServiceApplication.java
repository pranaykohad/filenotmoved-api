package com.filenotmoved.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import com.filenotmoved.user_service.entity.Role;
import com.filenotmoved.user_service.entity.IssueType;
import com.filenotmoved.user_service.repository.RoleRepository;
import com.filenotmoved.user_service.repository.IssueTypeRepository;
import java.util.Arrays;

@SpringBootApplication
@EntityScan("com.filenotmoved.user_service.entity")
@EnableJpaRepositories("com.filenotmoved.user_service.repository")
@EnableCaching
@Slf4j
public class UserServiceApplication {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private IssueTypeRepository issueTypeRepository;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
		log.info("User Service Application Started");
	}

	@PostConstruct
	public void initRoles() {
		if (roleRepository.count() == 0) {
			roleRepository.saveAll(Arrays.asList(
					new Role("USER"),
					new Role("ADMIN"),
					new Role("GOVT")));
		}
	}

	@PostConstruct
	public void initIssueTypes() {
		if (issueTypeRepository.count() == 0) {
			issueTypeRepository.saveAll(Arrays.asList(
					new IssueType("Road Damage"),
					new IssueType("Potholes"),
					new IssueType("Garbage"),
					new IssueType("Sewage"),
					new IssueType("Water Supply"),
					new IssueType("Street Light"),
					new IssueType("Drainage"),
					new IssueType("Electricity"),
					new IssueType("Traffic Issue"),
					new IssueType("Public Transport"),
					new IssueType("Property Issue"),
					new IssueType("Document Delay"),
					new IssueType("Certificate Delay"),
					new IssueType("Government Office Delay"),
					new IssueType("Other")));
			log.info("Issue types initialized successfully");
		}
	}

}
