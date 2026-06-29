package com.parking.system.service.impl;

import com.parking.system.dto.request.CreateUserRequest;
import com.parking.system.dto.request.UpdateUserPasswordRequest;
import com.parking.system.dto.request.UpdateUserRequest;
import com.parking.system.dto.request.UpdateUserStatusRequest;
import com.parking.system.dto.response.UserResponse;
import com.parking.system.entity.User;
import com.parking.system.enums.UserRole;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.UserRepository;
import com.parking.system.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse create(String actorUsername, CreateUserRequest request) {
        authorizeRoleManagement(getUserByUsername(actorUsername), request.getRole());
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }
        String email = normalizeEmail(request.getEmail());
        if (email != null && userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(email);
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse update(String actorUsername, Long id, UpdateUserRequest request) {
        User actor = getUserByUsername(actorUsername);
        User user = getUser(id);
        authorizeTargetManagement(actor, user);
        authorizeRoleManagement(actor, request.getRole());
        String email = normalizeEmail(request.getEmail());
        if (email != null && userRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new BusinessException("Email already exists");
        }
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return toResponse(getUser(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse updateStatus(String actorUsername, Long id, UpdateUserStatusRequest request) {
        User actor = getUserByUsername(actorUsername);
        User user = getUser(id);
        authorizeTargetManagement(actor, user);
        if (actor.getId().equals(user.getId())) {
            throw new BusinessException("Cannot change your own account status");
        }
        user.setStatus(request.getStatus());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updatePassword(String actorUsername, Long id, UpdateUserPasswordRequest request) {
        User actor = getUserByUsername(actorUsername);
        User user = getUser(id);
        authorizeTargetManagement(actor, user);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return toResponse(userRepository.save(user));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Current user not found"));
    }

    private void authorizeTargetManagement(User actor, User target) {
        if (actor.getRole() == UserRole.SYSTEM_ADMIN) {
            return;
        }
        if (target.getRole() == UserRole.SYSTEM_ADMIN || target.getRole() == UserRole.MANAGER) {
            throw new BusinessException("Only SYSTEM_ADMIN can manage administrator accounts");
        }
    }

    private void authorizeRoleManagement(User actor, UserRole requestedRole) {
        if (actor.getRole() == UserRole.SYSTEM_ADMIN) {
            return;
        }
        if (requestedRole == UserRole.SYSTEM_ADMIN || requestedRole == UserRole.MANAGER) {
            throw new BusinessException("Only SYSTEM_ADMIN can assign administrator roles");
        }
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private String normalizeEmail(String email) {
        return email == null || email.isBlank() ? null : email.trim().toLowerCase();
    }
}
