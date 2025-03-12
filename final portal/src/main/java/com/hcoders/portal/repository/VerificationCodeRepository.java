package com.hcoders.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcoders.portal.model.VerificationCode;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

	List<VerificationCode> findByUserIdAndTestId(Long userId, Long testId);

	
	
}
