package com.parking.system.service;

import com.parking.system.dto.request.CreateUserRequest;
import com.parking.system.dto.request.UpdateUserPasswordRequest;
import com.parking.system.dto.request.UpdateUserRequest;
import com.parking.system.dto.request.UpdateUserStatusRequest;
import com.parking.system.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse create(String actorUsername, CreateUserRequest request);
    UserResponse update(String actorUsername, Long id, UpdateUserRequest request);
    UserResponse get(Long id);
    List<UserResponse> getAll();
    UserResponse updateStatus(String actorUsername, Long id, UpdateUserStatusRequest request);
    UserResponse updatePassword(String actorUsername, Long id, UpdateUserPasswordRequest request);
}
