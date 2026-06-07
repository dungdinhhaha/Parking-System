package com.parking.system.service;

import com.parking.system.dto.request.CreateParkingBuildingRequest;
import com.parking.system.dto.request.UpdateParkingBuildingRequest;
import com.parking.system.dto.response.ParkingBuildingResponse;
import java.util.List;

public interface ParkingBuildingService {
    ParkingBuildingResponse create(CreateParkingBuildingRequest request);
    ParkingBuildingResponse update(Long id, UpdateParkingBuildingRequest request);
    ParkingBuildingResponse get(Long id);
    List<ParkingBuildingResponse> getAll();
    void delete(Long id);
}
