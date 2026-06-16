package com.parking.system.entity;

import com.parking.system.enums.SubscriptionCycleType;
import com.parking.system.enums.SubscriptionStatus;
import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingSubscription extends BaseEntity {
    private String subscriptionCode;
    private String rfidCardId;
    private VehicleType vehicleType;
    private SubscriptionCycleType subscriptionType;
    private SubscriptionStatus status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Vehicle vehicle;
    private ParkingZone assignedZone;
    private ParkingSlot assignedSlot;
}
