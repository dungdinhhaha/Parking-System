package com.parking.system.strategy.reservation;

import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ReservationAllocationStrategyFactory {

    private final Map<VehicleType, ReservationAllocationStrategy> strategies = new EnumMap<>(VehicleType.class);

    public ReservationAllocationStrategyFactory(List<ReservationAllocationStrategy> strategyList) {
        for (ReservationAllocationStrategy strategy : strategyList) {
            strategies.put(strategy.supports(), strategy);
        }
    }

    public ReservationAllocationStrategy getStrategy(VehicleType vehicleType) {
        ReservationAllocationStrategy strategy = strategies.get(vehicleType);
        if (strategy == null) {
            throw new BusinessException("Unsupported vehicle type");
        }
        return strategy;
    }
}
