package com.parking.system.stub;

import com.parking.system.entity.ParkingSubscription;
import com.parking.system.service.ParkingSubscriptionService;
import com.parking.system.strategy.allocation.AllocationResult;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class NoopParkingSubscriptionService implements ParkingSubscriptionService {

    @Override
    public Optional<ParkingSubscription> findActiveByRfidCardId(String rfidCardId, LocalDateTime atTime) {
        return Optional.empty();
    }

    @Override
    public Optional<AllocationResult> allocateForCheckIn(String rfidCardId, String plateNumber, LocalDateTime atTime) {
        return Optional.empty();
    }
}
