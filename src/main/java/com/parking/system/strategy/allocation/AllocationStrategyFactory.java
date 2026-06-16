package com.parking.system.strategy.allocation;

import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AllocationStrategyFactory {

    private final Map<VehicleType, ParkingAllocationStrategy> strategies = new EnumMap<>(VehicleType.class);

    public AllocationStrategyFactory(List<ParkingAllocationStrategy> strategyList) {
        for (ParkingAllocationStrategy strategy : strategyList) {
            strategies.put(strategy.supports(), strategy);
        }
    }

    public ParkingAllocationStrategy getStrategy(VehicleType vehicleType) {
        ParkingAllocationStrategy strategy = strategies.get(vehicleType);
        if (strategy == null) {
            throw new BusinessException("Unsupported vehicle type");
        }
        return strategy;
    }
}
