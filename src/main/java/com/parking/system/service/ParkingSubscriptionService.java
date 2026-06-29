package com.parking.system.service;

import com.parking.system.dto.request.CreateParkingSubscriptionRequest;
import com.parking.system.dto.request.RenewParkingSubscriptionRequest;
import com.parking.system.dto.request.UpdateParkingSubscriptionRequest;
import com.parking.system.dto.response.ParkingSubscriptionResponse;
import com.parking.system.dto.response.ParkingSubscriptionHistoryResponse;
import com.parking.system.entity.ParkingSubscription;
import com.parking.system.strategy.allocation.AllocationResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParkingSubscriptionService {
    ParkingSubscriptionResponse create(String username, CreateParkingSubscriptionRequest request);
    ParkingSubscriptionResponse get(Long id);
    List<ParkingSubscriptionResponse> getAll();
    List<ParkingSubscriptionResponse> getMine(String username);
    ParkingSubscriptionResponse findByPlateNumber(String plateNumber);
    List<ParkingSubscriptionResponse> findAllByPlateNumber(String plateNumber);
    ParkingSubscriptionResponse cancel(String username, Long id);
    ParkingSubscriptionResponse renew(String username, Long id, RenewParkingSubscriptionRequest request);
    ParkingSubscriptionResponse update(String username, Long id, UpdateParkingSubscriptionRequest request);
    List<ParkingSubscriptionHistoryResponse> getHistory(Long id);
    Optional<ParkingSubscription> findActiveByRfidCardId(String rfidCardId, LocalDateTime at);
    Optional<AllocationResult> allocateForCheckIn(String rfidCardId, String plateNumber, LocalDateTime at);
}
