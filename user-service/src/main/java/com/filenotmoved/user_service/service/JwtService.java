package com.filenotmoved.user_service.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.filenotmoved.user_service.entity.JwtToken;
import com.filenotmoved.user_service.exception.custom.JwtParseException;
import com.filenotmoved.user_service.repository.JwtTokenRepository;
import com.filenotmoved.user_service.util.JwtHelper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JwtService {

	private JwtTokenRepository jwtTokenRepository;
	private UserDetailsServiceImpl userDetailsService;

	// This EXPIRATION is just for storing expire date in database and not in jwt itself
	private static final LocalDateTime EXPIRATION = LocalDateTime.now().plusHours(24);

	public Boolean validateTokenForUserName(String token, String userName) {
		final UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
		boolean isTokenValidate = false;
		try {
			isTokenValidate = validateToken(token, userDetails.getUsername());
		} catch (Exception ex) {
			throw new JwtParseException("Invalid Jwt Token");
		}
		return isTokenValidate;
	}

	public void invalidateToken(String token) {
		jwtTokenRepository.deleteById(token);
	}

	@Transactional
	public String saveAndSendJwtTokenForSystemUser(String userName, Collection<? extends GrantedAuthority> collection) {
		return createAndSaveToken(userName, collection);
	}

	@Transactional
	public String generateTokenForAppUser(String phone, Collection<? extends GrantedAuthority> collection) {
		return createAndSaveToken(phone, collection);
	}

	public Boolean validateToken(String token, String useName) {
		final String username = JwtHelper.extractUsername(token);
		return (username.equals(useName) && !isTokenExpired(token));
	}

	private Boolean isTokenExpired(String token) {
		JwtToken jwtToken = jwtTokenRepository.findByToken(token);
		return jwtToken == null || jwtToken.getExpiration().isBefore(LocalDateTime.now());
	}

	private String createAndSaveToken(String id, Collection<? extends GrantedAuthority> collection) {
		final String authoritiesString = collection.stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		@SuppressWarnings("deprecation")
		final String token = Jwts.builder().setSubject(id).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
				.claim("authorities", authoritiesString).signWith(JwtHelper.getSignKey(), SignatureAlgorithm.HS256)
				.compact();

		jwtTokenRepository.deleteByUsername(id);

		JwtToken jwtToken = new JwtToken();
		jwtToken.setToken(token);
		jwtToken.setUsername(id);
		jwtToken.setExpiration(EXPIRATION);
		jwtTokenRepository.save(jwtToken);

		return token;
	}
}
