package com.parking.system.service;

public interface EmailService {
    void sendPasswordResetEmail(String recipient, String resetUrl);
}
