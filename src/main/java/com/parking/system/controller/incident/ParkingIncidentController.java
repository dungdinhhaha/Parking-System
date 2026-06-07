package com.parking.system.controller.incident;

import com.parking.system.dto.request.CreateIncidentRequest;
import com.parking.system.dto.request.ResolveIncidentRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.IncidentResponse;
import com.parking.system.enums.IncidentStatus;
import com.parking.system.service.ParkingIncidentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF','MANAGER','SYSTEM_ADMIN')")
public class ParkingIncidentController {

    private final ParkingIncidentService parkingIncidentService;

    @PostMapping
    public ApiResponse<IncidentResponse> create(Authentication authentication,
                                                @Valid @RequestBody CreateIncidentRequest request) {
        return ApiResponse.success(201, "Incident created successfully",
                parkingIncidentService.create(authentication.getName(), request));
    }

    @PatchMapping("/{id}/close")
    public ApiResponse<IncidentResponse> close(Authentication authentication,
                                               @PathVariable Long id,
                                               @Valid @RequestBody ResolveIncidentRequest request) {
        return ApiResponse.success("Incident resolved successfully",
                parkingIncidentService.close(authentication.getName(), id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<IncidentResponse> get(@PathVariable Long id) {
        return ApiResponse.success(parkingIncidentService.get(id));
    }

    @GetMapping
    public ApiResponse<List<IncidentResponse>> getAll(@RequestParam(required = false) IncidentStatus status) {
        return ApiResponse.success(parkingIncidentService.getByStatus(status));
    }

    @GetMapping("/session/{parkingSessionId}")
    public ApiResponse<List<IncidentResponse>> getBySession(@PathVariable Long parkingSessionId) {
        return ApiResponse.success(parkingIncidentService.getBySession(parkingSessionId));
    }
}
