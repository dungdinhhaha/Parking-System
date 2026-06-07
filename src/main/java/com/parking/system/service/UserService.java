package com.parking.system.service;

import com.parking.system.dto.request.CreateUserRequest;
import com.parking.system.dto.request.UpdateUserPasswordRequest;
import com.parking.system.dto.request.UpdateUserRequest;
import com.parking.system.dto.request.UpdateUserStatusRequest;
import com.parking.system.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse create(CreateUserRequest request);
    UserResponse update(Long id, UpdateUserRequest request);
    UserResponse get(Long id);
    List<UserResponse> getAll();
    UserResponse updateStatus(Long id, UpdateUserStatusRequest request);
    UserResponse updatePassword(Long id, UpdateUserPasswordRequest request);
}
