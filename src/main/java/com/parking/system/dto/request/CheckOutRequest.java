package com.parking.system.dto.request;

import com.parking.system.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CheckOutRequest {
    @NotNull
    private Long parkingSessionId;

    private String plateNumber;
    private String exitGate;
    private PaymentMethod paymentMethod = PaymentMethod.CASH;
    private LocalDateTime calculationTime;
    private Boolean lostTicket = Boolean.FALSE;
    private Boolean wrongZone = Boolean.FALSE;
    private MultipartFile plateImage;
}
