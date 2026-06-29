package com.parking.system.service.impl;

import com.parking.system.dto.request.ResetPasswordRequest;
import com.parking.system.entity.PasswordResetToken;
import com.parking.system.entity.User;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.PasswordResetTokenRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.service.EmailService;
import com.parking.system.service.PasswordResetService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;
    private static final int EXPIRY_MINUTES = 10;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${parking.frontend-base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    @Override
    @Transactional
    public void requestReset(String email) {
        userRepository.findByEmailIgnoreCase(email.trim()).ifPresent(this::createAndSendToken);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Password confirmation does not match");
        }

        PasswordResetToken token = tokenRepository.findByTokenHashForUpdate(hash(request.getToken()))
                .orElseThrow(() -> new BusinessException("Password reset link is invalid or expired"));
        LocalDateTime now = LocalDateTime.now();
        if (token.getUsedAt() != null || !token.getExpiresAt().isAfter(now)) {
            throw new BusinessException("Password reset link is invalid or expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        token.setUsedAt(now);
        userRepository.save(user);
        tokenRepository.save(token);
    }

    private void createAndSendToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.findAllByUser_IdAndUsedAtIsNull(user.getId()).forEach(token -> token.setUsedAt(now));

        String rawToken = generateToken();
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setTokenHash(hash(rawToken));
        token.setExpiresAt(now.plusMinutes(EXPIRY_MINUTES));
        tokenRepository.save(token);

        String resetUrl = frontendBaseUrl.replaceAll("/+$", "") + "/reset-password?token=" + rawToken;
        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
