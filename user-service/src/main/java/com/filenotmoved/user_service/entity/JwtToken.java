package com.filenotmoved.user_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtToken {

	@Id
	private String token;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private LocalDateTime expiration;

}
