package com.parking.system.repository;

import com.parking.system.entity.ParkingZone;
import com.parking.system.enums.ZoneStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingZoneRepository extends JpaRepository<ParkingZone, Long> {
    Optional<ParkingZone> findByFloor_IdAndZoneCodeIgnoreCase(Long floorId, String zoneCode);
    boolean existsByFloor_Id(Long floorId);
    List<ParkingZone> findAllByFloor_Id(Long floorId);
    List<ParkingZone> findAllByFloor_Building_BuildingCodeIgnoreCaseAndVehicleTypeAndStatus(String buildingCode, com.parking.system.enums.VehicleType vehicleType, ZoneStatus status);
}
