package com.parking.system.repository;

import com.parking.system.entity.ParkingFloor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingFloorRepository extends JpaRepository<ParkingFloor, Long> {
    Optional<ParkingFloor> findByBuilding_IdAndFloorCodeIgnoreCase(Long buildingId, String floorCode);
    boolean existsByBuilding_Id(Long buildingId);
    List<ParkingFloor> findAllByBuilding_Id(Long buildingId);
}
