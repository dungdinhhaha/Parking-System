package com.parking.system.repository;

import com.parking.system.entity.Vehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByOwner_IdAndPlateNumberIgnoreCase(Long ownerId, String plateNumber);
    Optional<Vehicle> findByPlateNumberIgnoreCase(String plateNumber);
    boolean existsByPlateNumberIgnoreCase(String plateNumber);
    List<Vehicle> findAllByOwner_UsernameOrderByCreatedAtDesc(String username);
    List<Vehicle> findAllByOrderByCreatedAtDesc();
}
