package com.parking.system.dto.response;

import com.parking.system.enums.UserRole;
import com.parking.system.enums.UserStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private final Long id;
    private final String fullName;
    private final String username;
    private final String email;
    private final String phone;
    private final UserRole role;
    private final UserStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
