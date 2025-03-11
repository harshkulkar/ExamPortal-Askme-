// Implementation of the VerificationCodeService interface.
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

@Service  // Indicates that this class is a Spring-managed service component.
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private UserRepository userRepository; // Repository to manage User data.

    @Autowired
    private VerificationCodeRepository verificationCodeRepository; // Repository to manage VerificationCode data.

    @Autowired
    private JavaMailSender mailSender; // Service for sending emails.

    // Assigns a candidate to a test by generating a verification code,
    // saving it to the repository, and emailing it to the candidate.
    @Override
    public void assignCandidate(Long userId, Long testId) {
        // Generate a random 6-digit verification code.
        String code = generateVerificationCode();

        // Create a new VerificationCode entity and set its properties.
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUserId(userId);
        verificationCode.setTestId(testId);
        verificationCode.setCode(code);
        // Optionally, set an expiry time for the verification code (e.g., 10 minutes from now).
        // verificationCode.setExpiry(LocalDateTime.now().plusMinutes(10));
        
        // Save the verification code entity to the repository.
        verificationCodeRepository.save(verificationCode);

        // Retrieve the user's email and send the verification code.
        sendVerificationCode(getUserEmail(userId), code);
    }

    // Generates a random 6-digit verification code as a String.
    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999)); // Ensures a 6-digit code with leading zeros if necessary.
    }

    // Sends the verification code to the specified email address.
    private void sendVerificationCode(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Exam Verification Code");
        message.setText("Your verification code is: " + code);
        mailSender.send(message);
    }

    // Retrieves the email address of a user by their userId.
    // Throws a RuntimeException if no user is found.
    private String getUserEmail(Long userId) {
        return userRepository.findById(userId)
                .map(User::getEmail)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    // Verifies if the provided code matches any of the stored verification codes for the user and test.
    // (Note: The expiry check is currently removed in this implementation.)
    @Override
    public boolean verifyCode(Long userId, Long testId, String code) {
        // Retrieve all stored verification codes for the given user and test.
        List<VerificationCode> storedCodes = verificationCodeRepository.findByUserIdAndTestId(userId, testId);

        // Check if any stored code exactly matches the provided code.
        return storedCodes.stream()
                .anyMatch(vc -> vc.getCode().equals(code));
    }
}
