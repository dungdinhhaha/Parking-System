package com.parking.system.repository;

import com.parking.system.entity.ParkingSlot;
import com.parking.system.enums.SlotStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    Optional<ParkingSlot> findByZone_IdAndSlotCodeIgnoreCase(Long zoneId, String slotCode);
    boolean existsByZone_Id(Long zoneId);
    boolean existsByIdAndStatusIsNot(Long id, SlotStatus status);
    List<ParkingSlot> findAllByZone_Id(Long zoneId);
    List<ParkingSlot> findAllByZone_Floor_Building_BuildingCodeIgnoreCaseAndStatus(String buildingCode, SlotStatus status);
}
