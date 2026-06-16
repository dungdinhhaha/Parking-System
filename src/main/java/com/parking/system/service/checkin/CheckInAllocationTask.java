package com.parking.system.service.checkin;

import com.parking.system.strategy.allocation.AllocationRequest;
import com.parking.system.strategy.allocation.AllocationResult;
import com.parking.system.strategy.allocation.AllocationStrategyFactory;
import org.springframework.stereotype.Service;

@Service
public class CheckInAllocationTask {

    private final AllocationStrategyFactory allocationStrategyFactory;

    public CheckInAllocationTask(AllocationStrategyFactory allocationStrategyFactory) {
        this.allocationStrategyFactory = allocationStrategyFactory;
    }

    public AllocationResult allocate(AllocationRequest request) {
        return allocationStrategyFactory.getStrategy(request.getVehicleType()).allocate(request);
    }
}
