package com.filenotmoved.user_service.repository;

import com.filenotmoved.user_service.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, String> {
    Optional<JwtToken> findByUsername(String username);
    void deleteByToken(String token);
}
