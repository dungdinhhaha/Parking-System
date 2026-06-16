package com.parking.system.dto.request;

public record CheckInRequest(String rfidCardId, String plateNumber, String vehicleType, String buildingCode) {
}
