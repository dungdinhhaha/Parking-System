package com.parking.system.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private final Long userId;
    private final String username;
    private final String role;
}
