package com.filenotmoved.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filenotmoved.user_service.entity.JwtToken;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, String> {

	JwtToken findByToken(String token);

	void deleteByUsername(String username);
}
