package com.parking.system.stub;

import com.parking.system.entity.Vehicle;
import com.parking.system.repository.VehicleRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class InMemoryVehicleRepository implements VehicleRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, Vehicle> vehicles = new LinkedHashMap<>();

    @Override
    public Optional<Vehicle> findByPlateNumberIgnoreCase(String plateNumber) {
        return vehicles.values().stream()
                .filter(vehicle -> vehicle.getPlateNumber() != null && vehicle.getPlateNumber().equalsIgnoreCase(plateNumber))
                .findFirst();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            vehicle.setId(sequence.getAndIncrement());
        }
        vehicles.put(vehicle.getId(), vehicle);
        return vehicle;
    }
}
