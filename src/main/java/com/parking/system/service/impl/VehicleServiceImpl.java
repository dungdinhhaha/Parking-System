package com.parking.system.service.impl;

import com.parking.system.dto.request.CreateVehicleRequest;
import com.parking.system.dto.request.UpdateVehicleRequest;
import com.parking.system.dto.response.VehicleResponse;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.enums.UserRole;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.UserRepository;
import com.parking.system.repository.VehicleRepository;
import com.parking.system.service.VehicleService;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public VehicleResponse create(String username, CreateVehicleRequest request) {
        User currentUser = getUserByUsername(username);
        User owner = resolveOwner(currentUser, request.getOwnerId());

        String plateNumber = normalizePlate(request.getPlateNumber());
        if (vehicleRepository.existsByPlateNumberIgnoreCase(plateNumber)) {
            throw new BusinessException("Vehicle plate number already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber(plateNumber);
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setColor(request.getColor());
        vehicle.setBrand(request.getBrand());
        vehicle.setOwner(owner);
        return toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public VehicleResponse update(String username, Long id, UpdateVehicleRequest request) {
        User currentUser = getUserByUsername(username);
        Vehicle vehicle = getOwnedVehicle(currentUser, id);

        String plateNumber = normalizePlate(request.getPlateNumber());
        vehicleRepository.findByPlateNumberIgnoreCase(plateNumber)
                .filter(existing -> !existing.getId().equals(vehicle.getId()))
                .ifPresent(existing -> {
                    throw new BusinessException("Vehicle plate number already exists");
                });

        vehicle.setPlateNumber(plateNumber);
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setColor(request.getColor());
        vehicle.setBrand(request.getBrand());
        return toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse get(String username, Long id) {
        User currentUser = getUserByUsername(username);
        return toResponse(getOwnedVehicle(currentUser, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getMyVehicles(String username) {
        return vehicleRepository.findAllByOwner_UsernameOrderByCreatedAtDesc(username).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAll() {
        return vehicleRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String username, Long id) {
        User currentUser = getUserByUsername(username);
        Vehicle vehicle = getOwnedVehicle(currentUser, id);
        vehicleRepository.delete(vehicle);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    private Vehicle getOwnedVehicle(User currentUser, Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Vehicle not found"));
        if (currentUser.getRole() != UserRole.MANAGER
                && currentUser.getRole() != UserRole.SYSTEM_ADMIN
                && (vehicle.getOwner() == null || !vehicle.getOwner().getId().equals(currentUser.getId()))) {
            throw new BusinessException("Access denied");
        }
        return vehicle;
    }

    private User resolveOwner(User currentUser, Long ownerId) {
        if (ownerId == null) {
            return currentUser;
        }
        if (currentUser.getRole() != UserRole.MANAGER && currentUser.getRole() != UserRole.SYSTEM_ADMIN) {
            throw new BusinessException("Access denied");
        }
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException("Owner user not found"));
    }

    private String normalizePlate(String plateNumber) {
        if (plateNumber == null || plateNumber.isBlank()) {
            throw new BusinessException("Plate number is required");
        }
        return plateNumber.trim().toUpperCase(Locale.ROOT);
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .plateNumber(vehicle.getPlateNumber())
                .vehicleType(vehicle.getVehicleType())
                .color(vehicle.getColor())
                .brand(vehicle.getBrand())
                .ownerId(vehicle.getOwner() != null ? vehicle.getOwner().getId() : null)
                .ownerUsername(vehicle.getOwner() != null ? vehicle.getOwner().getUsername() : null)
                .ownerFullName(vehicle.getOwner() != null ? vehicle.getOwner().getFullName() : null)
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
