package com.parking.system.service;

import com.parking.system.dto.request.CreateParkingZoneRequest;
import com.parking.system.dto.request.UpdateParkingZoneRequest;
import com.parking.system.dto.response.ParkingZoneResponse;
import java.util.List;

public interface ParkingZoneService {
    ParkingZoneResponse create(CreateParkingZoneRequest request);
    ParkingZoneResponse update(Long id, UpdateParkingZoneRequest request);
    ParkingZoneResponse get(Long id);
    List<ParkingZoneResponse> getAll();
    void delete(Long id);
}
