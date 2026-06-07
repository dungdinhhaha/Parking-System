package com.parking.system.service;

import com.parking.system.dto.request.CreateParkingSlotRequest;
import com.parking.system.dto.request.UpdateParkingSlotRequest;
import com.parking.system.dto.response.ParkingSlotResponse;
import java.util.List;

public interface ParkingSlotService {
    ParkingSlotResponse create(CreateParkingSlotRequest request);
    ParkingSlotResponse update(Long id, UpdateParkingSlotRequest request);
    ParkingSlotResponse get(Long id);
    List<ParkingSlotResponse> getAll();
    void delete(Long id);
}
