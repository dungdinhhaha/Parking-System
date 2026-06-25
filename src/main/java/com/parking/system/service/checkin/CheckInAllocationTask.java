package com.parking.system.service.checkin;

import com.parking.system.strategy.allocation.AllocationRequest;
import com.parking.system.strategy.allocation.AllocationResult;
import com.parking.system.strategy.allocation.AllocationStrategyFactory;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.springframework.stereotype.Component;

@Component
public class CheckInAllocationTask {

    private final AllocationStrategyFactory allocationStrategyFactory;

    public CheckInAllocationTask(AllocationStrategyFactory allocationStrategyFactory) {
        this.allocationStrategyFactory = allocationStrategyFactory;
    }

    public CompletableFuture<AllocationResult> allocateAsync(AllocationRequest request, Executor executor) {
        return CompletableFuture.supplyAsync(() -> allocationStrategyFactory
                .getStrategy(request.getVehicleType())
                .allocate(request), executor);
    }

    public AllocationResult allocate(AllocationRequest request) {
        return allocationStrategyFactory
                .getStrategy(request.getVehicleType())
                .allocate(request);
    }
}
