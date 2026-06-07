package com.parking.system.controller.building;

import com.parking.system.dto.request.CreateParkingBuildingRequest;
import com.parking.system.dto.request.UpdateParkingBuildingRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.ParkingBuildingResponse;
import com.parking.system.service.ParkingBuildingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
public class ParkingBuildingController {

    private final ParkingBuildingService parkingBuildingService;

    @PostMapping
    public ApiResponse<ParkingBuildingResponse> create(@Valid @RequestBody CreateParkingBuildingRequest request) {
        return ApiResponse.success(201, "Building created successfully", parkingBuildingService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ParkingBuildingResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateParkingBuildingRequest request) {
        return ApiResponse.success("Building updated successfully", parkingBuildingService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ParkingBuildingResponse> get(@PathVariable Long id) {
        return ApiResponse.success(parkingBuildingService.get(id));
    }

    @GetMapping
    public ApiResponse<List<ParkingBuildingResponse>> getAll() {
        return ApiResponse.success(parkingBuildingService.getAll());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        parkingBuildingService.delete(id);
        return ApiResponse.success("Building deleted successfully", null);
    }
}
