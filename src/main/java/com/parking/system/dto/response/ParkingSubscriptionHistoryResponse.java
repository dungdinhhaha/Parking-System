package com.parking.system.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingSubscriptionHistoryResponse {
    private Long id;
    private Long subscriptionId;
    private String action;
    private String oldRfidCardId;
    private String newRfidCardId;
    private Long oldVehicleId;
    private Long newVehicleId;
    private Long oldZoneId;
    private Long newZoneId;
    private Long oldSlotId;
    private Long newSlotId;
    private String changedByUsername;
    private String notes;
    private LocalDateTime createdAt;
}
