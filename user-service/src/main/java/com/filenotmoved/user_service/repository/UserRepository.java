package com.filenotmoved.user_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.filenotmoved.user_service.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	User findByPhone(String phone);

	void deleteByPhone(String phone);

	User findByPhoneAndOtp(String phone, String otp);

	@Query("SELECT a FROM User a WHERE a.otpTimeStamp < :otpTimeStamp")
	List<User> findByOtpDateTime(@Param("otpTimeStamp") LocalDateTime otpTimeStamp);
	
}