package com.parking.system.repository;

import com.parking.system.entity.PricingPolicy;
import com.parking.system.enums.PolicyStatus;
import com.parking.system.enums.VehicleType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingPolicyRepository extends JpaRepository<PricingPolicy, Long> {
    List<PricingPolicy> findAllByVehicleTypeAndStatusOrderByCreatedAtDesc(VehicleType vehicleType, PolicyStatus status);
}
