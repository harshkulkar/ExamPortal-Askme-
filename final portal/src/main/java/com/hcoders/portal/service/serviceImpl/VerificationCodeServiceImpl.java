package com.hcoders.portal.service.serviceImpl;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.hcoders.portal.model.User;
import com.hcoders.portal.model.VerificationCode;
import com.hcoders.portal.repository.UserRepository;
import com.hcoders.portal.repository.VerificationCodeRepository;
import com.hcoders.portal.service.VerificationCodeService;
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void assignCandidate(Long userId, Long testId) {
        String code = generateVerificationCode();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUserId(userId);
        verificationCode.setTestId(testId);
        verificationCode.setCode(code);
//        verificationCode.setExpiry(LocalDateTime.now().plusMinutes(10));
        verificationCodeRepository.save(verificationCode);

        sendVerificationCode(getUserEmail(userId), code);
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999)); // 6-digit code
    }

    private void sendVerificationCode(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Exam Verification Code");
        message.setText("Your verification code is: " + code);
        mailSender.send(message);
    }

    private String getUserEmail(Long userId) {
        return userRepository.findById(userId)
                .map(User::getEmail)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Override
    public boolean verifyCode(Long userId, Long testId, String code) {
        List<VerificationCode> storedCodes = verificationCodeRepository.findByUserIdAndTestId(userId, testId);

        // Check if any verification code in the list matches the entered code (Removed expiry condition)
        return storedCodes.stream()
                .anyMatch(vc -> vc.getCode().equals(code));  // ❌ Removed expiry check
    }

}
