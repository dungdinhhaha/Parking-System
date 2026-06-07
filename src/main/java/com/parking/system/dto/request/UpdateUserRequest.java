package com.parking.system.dto.request;

import com.parking.system.enums.UserRole;
import com.parking.system.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    @NotBlank
    private String fullName;

    @Email
    private String email;

    private String phone;

    @NotNull
    private UserRole role;

    @NotNull
    private UserStatus status;
}
