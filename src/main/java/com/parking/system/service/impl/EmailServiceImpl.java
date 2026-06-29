package com.parking.system.service.impl;

import com.parking.system.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendPasswordResetEmail(String recipient, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject("ParkFlow - Dat lai mat khau");
        message.setText("""
                Ban da yeu cau dat lai mat khau ParkFlow.

                Mo lien ket sau trong vong 10 phut:
                %s

                Neu ban khong thuc hien yeu cau nay, hay bo qua email.
                """.formatted(resetUrl));
        mailSender.send(message);
    }
}
