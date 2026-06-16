package com.parking.system.stub;

import com.parking.system.entity.Reservation;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InMemoryReservationRepository implements ReservationRepository {

    @Override
    public List<Reservation> findMatchingForUpdate(String plateNumber, VehicleType vehicleType, ReservationStatus status, LocalDateTime atTime) {
        return List.of();
    }
}
