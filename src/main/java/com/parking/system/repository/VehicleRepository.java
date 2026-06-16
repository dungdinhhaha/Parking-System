package com.parking.system.repository;

import com.parking.system.entity.Vehicle;
import java.util.Optional;

public interface VehicleRepository {
    Optional<Vehicle> findByPlateNumberIgnoreCase(String plateNumber);
    Vehicle save(Vehicle vehicle);
}
