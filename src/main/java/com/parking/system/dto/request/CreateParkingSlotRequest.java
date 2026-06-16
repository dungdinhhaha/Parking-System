package com.parking.system.dto.request;

public record CreateParkingSlotRequest(String zoneCode, String slotCode, String vehicleType) {
}
