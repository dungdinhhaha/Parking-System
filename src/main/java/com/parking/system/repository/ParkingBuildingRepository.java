package com.parking.system.repository;

import com.parking.system.entity.ParkingBuilding;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingBuildingRepository extends JpaRepository<ParkingBuilding, Long> {
    Optional<ParkingBuilding> findByBuildingCodeIgnoreCase(String buildingCode);
    List<ParkingBuilding> findAllByBuildingCodeIgnoreCase(String buildingCode);
    boolean existsByBuildingCodeIgnoreCase(String buildingCode);
    Optional<ParkingBuilding> findByNameIgnoreCase(String name);
    List<ParkingBuilding> findAllByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
