package com.parking.system.repository;

import com.parking.system.entity.ParkingSlot;
import com.parking.system.enums.SlotStatus;
import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository {
    List<ParkingSlot> findAllAvailableForUpdate(String buildingCode, SlotStatus status);
    Optional<ParkingSlot> findByIdForUpdate(Long id);
    ParkingSlot save(ParkingSlot slot);
}
