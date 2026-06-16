package com.parking.system.dto.request;

public record CheckOutRequest(String rfidCardId, String plateNumber, String parkingSessionId) {
}
