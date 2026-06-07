package com.parking.system.service.checkout;

import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.enums.VehicleType;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckOutResourceReleaser {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingZoneRepository parkingZoneRepository;

    public void release(ParkingSession session) {
        if (session.getVehicleType() == VehicleType.CAR) {
            releaseSlot(session.getAssignedSlot());
            return;
        }
        releaseZone(session.getAssignedZone());
    }

    private void releaseSlot(ParkingSlot slot) {
        if (slot == null) {
            return;
        }
        slot.release();
        parkingSlotRepository.save(slot);
    }

    private void releaseZone(ParkingZone zone) {
        if (zone == null) {
            return;
        }
        zone.decreaseCurrentCount();
        parkingZoneRepository.save(zone);
    }
}
