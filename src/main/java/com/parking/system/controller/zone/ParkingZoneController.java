package com.parking.system.controller.zone;

import com.parking.system.dto.request.CreateParkingZoneRequest;
import com.parking.system.dto.request.UpdateParkingZoneRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.ParkingZoneResponse;
import com.parking.system.service.ParkingZoneService;
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
@RequestMapping("/api/zones")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
public class ParkingZoneController {

    private final ParkingZoneService parkingZoneService;

    @PostMapping
    public ApiResponse<ParkingZoneResponse> create(@Valid @RequestBody CreateParkingZoneRequest request) {
        return ApiResponse.success(201, "Zone created successfully", parkingZoneService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ParkingZoneResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateParkingZoneRequest request) {
        return ApiResponse.success("Zone updated successfully", parkingZoneService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ParkingZoneResponse> get(@PathVariable Long id) {
        return ApiResponse.success(parkingZoneService.get(id));
    }

    @GetMapping
    public ApiResponse<List<ParkingZoneResponse>> getAll() {
        return ApiResponse.success(parkingZoneService.getAll());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        parkingZoneService.delete(id);
        return ApiResponse.success("Zone deleted successfully", null);
    }
}
