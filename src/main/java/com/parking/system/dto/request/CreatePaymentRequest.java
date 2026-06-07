package com.parking.system.dto.request;

import com.parking.system.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {
    @NotNull
    private Long parkingSessionId;

    @NotNull
    private PaymentMethod method;

    private LocalDateTime calculationTime;
    private Boolean lostTicket = Boolean.FALSE;
    private Boolean wrongZone = Boolean.FALSE;
}
