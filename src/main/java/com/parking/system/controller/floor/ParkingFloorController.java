package com.parking.system.controller.floor;

import com.parking.system.dto.request.CreateParkingFloorRequest;
import com.parking.system.dto.request.UpdateParkingFloorRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.ParkingFloorResponse;
import com.parking.system.service.ParkingFloorService;
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
@RequestMapping("/api/floors")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
public class ParkingFloorController {

    private final ParkingFloorService parkingFloorService;

    @PostMapping
    public ApiResponse<ParkingFloorResponse> create(@Valid @RequestBody CreateParkingFloorRequest request) {
        return ApiResponse.success(201, "Floor created successfully", parkingFloorService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ParkingFloorResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateParkingFloorRequest request) {
        return ApiResponse.success("Floor updated successfully", parkingFloorService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ParkingFloorResponse> get(@PathVariable Long id) {
        return ApiResponse.success(parkingFloorService.get(id));
    }

    @GetMapping
    public ApiResponse<List<ParkingFloorResponse>> getAll() {
        return ApiResponse.success(parkingFloorService.getAll());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        parkingFloorService.delete(id);
        return ApiResponse.success("Floor deleted successfully", null);
    }
}
