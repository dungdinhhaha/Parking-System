package com.parking.system.controller.auth;

import com.parking.system.dto.request.LoginRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.AuthResponse;
import com.parking.system.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
        AuthResponse response = authService.login(request);
        httpServletRequest.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
        return ApiResponse.success(HttpStatus.OK.value(), "Login successful", response);
    }

    @GetMapping("/me")
    public ApiResponse<AuthResponse> me(Authentication authentication) {
        return ApiResponse.success(HttpStatus.OK.value(), "Current user", authService.me(authentication.getName()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ApiResponse.success(HttpStatus.OK.value(), "Logout successful", null);
    }
}
