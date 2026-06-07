package com.parking.system.dto.response;

import com.parking.system.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private Long userId;
    private String username;
    private String fullName;
    private UserRole role;
}
