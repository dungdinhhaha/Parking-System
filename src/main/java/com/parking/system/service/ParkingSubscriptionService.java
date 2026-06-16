package com.parking.system.service;

import com.parking.system.entity.ParkingSubscription;
import com.parking.system.strategy.allocation.AllocationResult;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ParkingSubscriptionService {
    Optional<ParkingSubscription> findActiveByRfidCardId(String rfidCardId, LocalDateTime atTime);
    Optional<AllocationResult> allocateForCheckIn(String rfidCardId, String plateNumber, LocalDateTime atTime);
}
