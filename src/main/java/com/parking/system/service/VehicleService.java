package com.parking.system.service;

import com.parking.system.dto.request.CreateVehicleRequest;
import com.parking.system.dto.request.UpdateVehicleRequest;
import com.parking.system.dto.response.VehicleResponse;
import java.util.List;

public interface VehicleService {
    VehicleResponse create(String username, CreateVehicleRequest request);
    VehicleResponse update(String username, Long id, UpdateVehicleRequest request);
    VehicleResponse get(String username, Long id);
    List<VehicleResponse> getMyVehicles(String username);
    List<VehicleResponse> getAll();
    void delete(String username, Long id);
}
