package com.parking.system.controller.user;

import com.parking.system.dto.request.CreateUserRequest;
import com.parking.system.dto.request.UpdateUserPasswordRequest;
import com.parking.system.dto.request.UpdateUserRequest;
import com.parking.system.dto.request.UpdateUserStatusRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.UserResponse;
import com.parking.system.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success(201, "User created successfully", userService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.success("User updated successfully", userService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> get(@PathVariable Long id) {
        return ApiResponse.success(userService.get(id));
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAll() {
        return ApiResponse.success(userService.getAll());
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UserResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest request) {
        return ApiResponse.success("User status updated successfully", userService.updateStatus(id, request));
    }

    @PatchMapping("/{id}/password")
    public ApiResponse<UserResponse> updatePassword(@PathVariable Long id, @Valid @RequestBody UpdateUserPasswordRequest request) {
        return ApiResponse.success("User password updated successfully", userService.updatePassword(id, request));
    }
}
