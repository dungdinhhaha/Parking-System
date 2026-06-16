package com.parking.system.dto.request;

import com.parking.system.enums.GateActionType;
import com.parking.system.enums.VehicleType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckInRequest {
    private String requestId;
    private GateActionType action;
    private String rfidCardId;
    private String plateNumber;
    private VehicleType vehicleType;
    private String buildingCode;
    private String entryGate;
    private LocalDateTime allocationTime;
}
