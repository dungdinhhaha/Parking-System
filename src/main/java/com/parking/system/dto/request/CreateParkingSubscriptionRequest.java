package com.parking.system.dto.request;

public record CreateParkingSubscriptionRequest(String plateNumber, String vehicleType, String cycleType) {
}
