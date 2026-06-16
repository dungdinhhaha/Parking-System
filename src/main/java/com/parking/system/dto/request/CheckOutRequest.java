package com.parking.system.dto.request;

import com.parking.system.enums.GateActionType;
import com.parking.system.enums.PaymentMethod;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CheckOutRequest {
    private String requestId;
    private GateActionType action;
    private Long parkingSessionId;
    private String rfidCardId;
    private String plateNumber;
    private String exitGate;
    private PaymentMethod paymentMethod;
    private Boolean lostTicket;
    private Boolean wrongZone;
    private LocalDateTime calculationTime;
    private MultipartFile plateImage;
}
