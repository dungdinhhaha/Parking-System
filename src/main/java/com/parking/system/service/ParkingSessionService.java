package com.parking.system.service;

import com.parking.system.dto.request.CheckInRequest;
import com.parking.system.dto.response.ParkingSessionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ParkingSessionService {
    ParkingSessionResponse checkIn(String username, CheckInRequest request, MultipartFile plateImage);
}
