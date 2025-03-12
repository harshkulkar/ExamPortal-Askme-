package com.hcoders.portal.service;

public interface VerificationCodeService {
    void assignCandidate(Long userId, Long testId);
    boolean verifyCode(Long userId, Long testId, String code);
}
