package com.parking.system.service;

import com.parking.system.dto.request.CreateReservationRequest;
import com.parking.system.dto.response.ReservationResponse;
import com.parking.system.dto.response.ReservationAvailabilityResponse;
import java.util.List;

public interface ReservationService {
    ReservationResponse create(String username, CreateReservationRequest request);
    ReservationResponse cancel(String username, Long reservationId);
    ReservationResponse get(String username, Long reservationId);
    List<ReservationResponse> getMyReservations(String username);
    List<ReservationResponse> getAll();
    ReservationAvailabilityResponse getAvailability(String buildingCode);
}
