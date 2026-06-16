package com.parking.system.entity;

import com.parking.system.enums.PaymentMethod;
import com.parking.system.enums.PaymentStatus;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment extends BaseEntity {
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod method;
    private ParkingSession parkingSession;
}
