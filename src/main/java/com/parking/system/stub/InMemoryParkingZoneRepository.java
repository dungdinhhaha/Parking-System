package com.parking.system.stub;

import com.parking.system.entity.ParkingZone;
import com.parking.system.enums.VehicleType;
import com.parking.system.enums.ZoneStatus;
import com.parking.system.repository.ParkingZoneRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InMemoryParkingZoneRepository implements ParkingZoneRepository {

    @Override
    public List<ParkingZone> findAllAvailableForUpdate(String buildingCode, VehicleType vehicleType, ZoneStatus status) {
        return List.of();
    }

    @Override
    public Optional<ParkingZone> findByIdForUpdate(Long id) {
        return Optional.empty();
    }

    @Override
    public ParkingZone save(ParkingZone zone) {
        return zone;
    }
}
