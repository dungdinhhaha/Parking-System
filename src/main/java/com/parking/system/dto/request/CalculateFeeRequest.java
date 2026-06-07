package com.parking.system.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateFeeRequest {
    @NotNull
    private Long parkingSessionId;

    private LocalDateTime calculationTime;
    private Boolean lostTicket = Boolean.FALSE;
    private Boolean wrongZone = Boolean.FALSE;
}
