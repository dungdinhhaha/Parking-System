package com.parking.system.entity;

import com.parking.system.enums.UserRole;
import com.parking.system.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseEntity {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private UserRole role;
    private UserStatus status;
}
