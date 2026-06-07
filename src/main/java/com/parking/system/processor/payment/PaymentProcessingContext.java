package com.parking.system.processor.payment;

import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.Payment;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentProcessingContext {
    private final ParkingSession parkingSession;
    private final Payment payment;
    private final BigDecimal amount;
}
