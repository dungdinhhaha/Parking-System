package com.parking.system.dto.response;

import com.parking.system.enums.VehicleType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationAvailabilityResponse {
    private String buildingCode;
    private String buildingName;
    private List<ParkingSlotResponse> availableCarSlots;
    private List<ParkingZoneResponse> availableMotorbikeZones;
    private Long carSlotCount;
    private Long motorbikeZoneCount;
    private VehicleType selectedVehicleType;
}
