package com.parking.system.service.impl;

import com.parking.system.dto.request.CheckInRequest;
import com.parking.system.dto.response.ParkingSessionResponse;
import com.parking.system.facade.ParkingOperationFacade;
import com.parking.system.service.ParkingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ParkingSessionServiceImpl implements ParkingSessionService {

    private final ParkingOperationFacade parkingOperationFacade;

    @Override
    public ParkingSessionResponse checkIn(String username, CheckInRequest request, MultipartFile plateImage) {
        return parkingOperationFacade.checkIn(username, request, plateImage);
    }
}
