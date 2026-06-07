package com.parking.system.service;

import com.parking.system.dto.request.LoginRequest;
import com.parking.system.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse me(String username);
}
