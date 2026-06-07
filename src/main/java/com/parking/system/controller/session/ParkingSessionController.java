package com.parking.system.controller.session;

import com.parking.system.dto.request.CheckInRequest;
import com.parking.system.dto.request.CheckOutRequest;
import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.CheckOutResponse;
import com.parking.system.dto.response.ParkingSessionResponse;
import com.parking.system.facade.CheckOutFacade;
import com.parking.system.service.ParkingSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class ParkingSessionController {

    private final ParkingSessionService parkingSessionService;
    private final CheckOutFacade checkOutFacade;

    @PreAuthorize("hasAnyRole('MANAGER','STAFF','SYSTEM_ADMIN')")
    @PostMapping(value = "/check-in", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ParkingSessionResponse> checkIn(Authentication authentication,
                                                       @Valid @ModelAttribute CheckInRequest request,
                                                       @RequestPart(value = "plateImage", required = false) MultipartFile plateImage) {
        return ApiResponse.success(201, "Check-in completed successfully",
                parkingSessionService.checkIn(authentication.getName(), request, plateImage));
    }

    @PreAuthorize("hasAnyRole('MANAGER','STAFF','SYSTEM_ADMIN')")
    @PostMapping(value = "/check-out", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CheckOutResponse> checkOut(Authentication authentication,
                                                  @Valid @ModelAttribute CheckOutRequest request) {
        return ApiResponse.success(200, "Check-out completed successfully",
                checkOutFacade.checkOut(authentication.getName(), request));
    }
}
