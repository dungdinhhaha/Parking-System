package com.parking.system.service.reservation;

import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.Reservation;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationResourceManager {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingZoneRepository parkingZoneRepository;

    @Transactional
    public void release(Reservation reservation) {
        ParkingSlot slot = reservation.getAssignedSlot();
        if (slot != null && slot.isReserved()) {
            ParkingSlot lockedSlot = parkingSlotRepository.findByIdForUpdate(slot.getId()).orElse(null);
            if (lockedSlot != null && lockedSlot.isReserved()) {
                lockedSlot.release();
                parkingSlotRepository.save(lockedSlot);
            }
        }

        ParkingZone zone = reservation.getAssignedZone();
        if (zone != null && slot == null) {
            ParkingZone lockedZone = parkingZoneRepository.findByIdForUpdate(zone.getId()).orElse(null);
            if (lockedZone != null) {
                lockedZone.decreaseReservedCount();
                parkingZoneRepository.save(lockedZone);
            }
        }
    }
}
