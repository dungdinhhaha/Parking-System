package com.parking.system.controller.auth;

import com.parking.system.dto.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/test")
public class AuthTestController {

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> me(Authentication authentication) {
        return ApiResponse.success("Authenticated as: " + authentication.getName(), null);
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<String> manager() {
        return ApiResponse.success("Access granted for MANAGER", null);
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF')")
    public ApiResponse<String> staff() {
        return ApiResponse.success("Access granted for STAFF", null);
    }

    @GetMapping("/driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ApiResponse<String> driver() {
        return ApiResponse.success("Access granted for DRIVER", null);
    }

    @GetMapping("/system-admin")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<String> systemAdmin() {
        return ApiResponse.success("Access granted for SYSTEM_ADMIN", null);
    }
}
