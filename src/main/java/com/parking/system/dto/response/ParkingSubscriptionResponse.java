package com.parking.system.dto.response;

import com.parking.system.enums.SubscriptionCycleType;
import com.parking.system.enums.SubscriptionStatus;
import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingSubscriptionResponse {
    private final Long id;
    private final String subscriptionCode;
    private final String rfidCardId;
    private final String subscriberName;
    private final String subscriberPhone;
    private final SubscriptionCycleType subscriptionType;
    private final SubscriptionStatus status;
    private final VehicleType vehicleType;
    private final Long vehicleId;
    private final String vehiclePlateNumber;
    private final Long assignedZoneId;
    private final String assignedZoneCode;
    private final Long assignedSlotId;
    private final String assignedSlotCode;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final Long createdById;
    private final String createdByUsername;
    private final String notes;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
