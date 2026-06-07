package com.parking.system.service;

import com.parking.system.dto.request.CreateParkingFloorRequest;
import com.parking.system.dto.request.UpdateParkingFloorRequest;
import com.parking.system.dto.response.ParkingFloorResponse;
import java.util.List;

public interface ParkingFloorService {
    ParkingFloorResponse create(CreateParkingFloorRequest request);
    ParkingFloorResponse update(Long id, UpdateParkingFloorRequest request);
    ParkingFloorResponse get(Long id);
    List<ParkingFloorResponse> getAll();
    void delete(Long id);
}
