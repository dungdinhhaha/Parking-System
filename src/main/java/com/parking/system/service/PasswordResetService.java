package com.parking.system.service;

import com.parking.system.dto.request.ResetPasswordRequest;

public interface PasswordResetService {
    void requestReset(String email);
    void resetPassword(ResetPasswordRequest request);
}
