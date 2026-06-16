package com.parking.system.facade;

import com.parking.system.dto.request.CheckInRequest;
import com.parking.system.dto.response.ParkingSessionResponse;
import com.parking.system.service.checkin.CheckInOrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ParkingOperationFacade {

    private final CheckInOrchestrationService checkInOrchestrationService;

    @Transactional
    public ParkingSessionResponse checkIn(String username, CheckInRequest request, MultipartFile plateImage) {
        return checkInOrchestrationService.checkIn(username, request, plateImage);
    }
}
