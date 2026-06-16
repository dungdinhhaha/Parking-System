package com.parking.system.stub;

import com.parking.system.entity.ParkingSlot;
import com.parking.system.enums.SlotStatus;
import com.parking.system.repository.ParkingSlotRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InMemoryParkingSlotRepository implements ParkingSlotRepository {

    @Override
    public List<ParkingSlot> findAllAvailableForUpdate(String buildingCode, SlotStatus status) {
        return List.of();
    }

    @Override
    public Optional<ParkingSlot> findByIdForUpdate(Long id) {
        return Optional.empty();
    }

    @Override
    public ParkingSlot save(ParkingSlot slot) {
        return slot;
    }
}
