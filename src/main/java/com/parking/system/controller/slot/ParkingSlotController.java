package com.parking.system.controller.slot;

import com.parking.system.dto.request.CreateParkingSlotRequest;
import com.parking.system.dto.request.UpdateParkingSlotRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.ParkingSlotResponse;
import com.parking.system.service.ParkingSlotService;
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
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
public class ParkingSlotController {

    private final ParkingSlotService parkingSlotService;

    @PostMapping
    public ApiResponse<ParkingSlotResponse> create(@Valid @RequestBody CreateParkingSlotRequest request) {
        return ApiResponse.success(201, "Slot created successfully", parkingSlotService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ParkingSlotResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateParkingSlotRequest request) {
        return ApiResponse.success("Slot updated successfully", parkingSlotService.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ParkingSlotResponse> get(@PathVariable Long id) {
        return ApiResponse.success(parkingSlotService.get(id));
    }

    @GetMapping
    public ApiResponse<List<ParkingSlotResponse>> getAll() {
        return ApiResponse.success(parkingSlotService.getAll());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        parkingSlotService.delete(id);
        return ApiResponse.success("Slot deleted successfully", null);
    }
}
