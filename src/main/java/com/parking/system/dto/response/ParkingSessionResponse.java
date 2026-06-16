package com.parking.system.dto.response;

import com.parking.system.enums.SessionStatus;
import com.parking.system.enums.SubscriptionCycleType;
import com.parking.system.enums.VehicleType;
import com.parking.system.strategy.allocation.AllocationSource;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingSessionResponse {
    private final Long id;
    private final String ticketCode;
    private final String checkInRequestId;
    private final String plateNumber;
    private final String rfidCardId;
    private final VehicleType vehicleType;
    private final SessionStatus status;
    private final String entryGate;
    private final LocalDateTime checkInTime;
    private final Long zoneId;
    private final String zoneCode;
    private final Long slotId;
    private final String slotCode;
    private final Long reservationId;
    private final String reservationCode;
    private final Long subscriptionId;
    private final String subscriptionCode;
    private final SubscriptionCycleType subscriptionType;
    private final Long recognitionLogId;
    private final AllocationSource allocationSource;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
