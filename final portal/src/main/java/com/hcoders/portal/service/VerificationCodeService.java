// Service interface for managing verification codes for exam candidates.
package com.hcoders.portal.service;

public interface VerificationCodeService {
    // Assigns a candidate to a test by generating and sending a verification code.
    void assignCandidate(Long userId, Long testId);
    
    // Verifies if the provided verification code is valid for the given user and test.
    boolean verifyCode(Long userId, Long testId, String code);
}
