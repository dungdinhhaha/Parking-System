package com.parking.system.repository;

import com.parking.system.entity.ParkingZone;
import com.parking.system.enums.VehicleType;
import com.parking.system.enums.ZoneStatus;
import java.util.List;
import java.util.Optional;

public interface ParkingZoneRepository {
    List<ParkingZone> findAllAvailableForUpdate(String buildingCode, VehicleType vehicleType, ZoneStatus status);
    Optional<ParkingZone> findByIdForUpdate(Long id);
    ParkingZone save(ParkingZone zone);
}
