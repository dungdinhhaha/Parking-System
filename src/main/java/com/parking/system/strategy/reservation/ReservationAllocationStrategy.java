package com.parking.system.strategy.reservation;

import com.parking.system.dto.request.CreateReservationRequest;
import com.parking.system.enums.VehicleType;

public interface ReservationAllocationStrategy {
    VehicleType supports();

    ReservationAllocationResult allocate(CreateReservationRequest request);
}
