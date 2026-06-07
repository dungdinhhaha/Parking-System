package com.parking.system.service.impl;

import com.parking.system.dto.request.CreateReservationRequest;
import com.parking.system.dto.response.ReservationAvailabilityResponse;
import com.parking.system.dto.response.ReservationResponse;
import com.parking.system.facade.ReservationFacade;
import com.parking.system.service.ReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationFacade reservationFacade;

    @Override
    public ReservationResponse create(String username, CreateReservationRequest request) {
        return reservationFacade.create(username, request);
    }

    @Override
    public ReservationResponse cancel(String username, Long reservationId) {
        return reservationFacade.cancel(username, reservationId);
    }

    @Override
    public ReservationResponse get(String username, Long reservationId) {
        return reservationFacade.get(username, reservationId);
    }

    @Override
    public List<ReservationResponse> getMyReservations(String username) {
        return reservationFacade.getMyReservations(username);
    }

    @Override
    public List<ReservationResponse> getAll() {
        return reservationFacade.getAll();
    }

    @Override
    public ReservationAvailabilityResponse getAvailability(String buildingCode) {
        return reservationFacade.getAvailability(buildingCode);
    }
}
