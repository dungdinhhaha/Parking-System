package com.parking.system.dto.response;

import com.parking.system.enums.SessionStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.strategy.allocation.AllocationSource;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingSessionResponse {
    private Long id;
    private String ticketCode;
    private String plateNumber;
    private VehicleType vehicleType;
    private SessionStatus status;
    private String entryGate;
    private LocalDateTime checkInTime;
    private Long zoneId;
    private String zoneCode;
    private Long slotId;
    private String slotCode;
    private Long reservationId;
    private String reservationCode;
    private Long recognitionLogId;
    private AllocationSource allocationSource;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
