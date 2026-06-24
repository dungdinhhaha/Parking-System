package com.parking.system.dto.response;

import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResponse {
    private Long id;
    private String reservationCode;
    private String plateNumber;
    private VehicleType vehicleType;
    private ReservationStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime usedAt;
    private Long userId;
    private String username;
    private Long zoneId;
    private String zoneCode;
    private Long slotId;
    private String slotCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
// Edited by Codex
