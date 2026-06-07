package com.parking.system.service.checkout;

import com.parking.system.dto.response.CheckOutResponse;
import com.parking.system.entity.Payment;
import com.parking.system.entity.ParkingSession;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class CheckOutMapper {

    public CheckOutResponse map(ParkingSession session, Payment payment, BigDecimal feeAmount, String message) {
        return CheckOutResponse.builder()
                .parkingSessionId(session.getId())
                .ticketCode(session.getTicketCode())
                .plateNumber(session.getPlateNumber())
                .exitGate(session.getExitGate())
                .checkOutTime(session.getCheckOutTime())
                .feeAmount(feeAmount)
                .paymentId(payment != null ? payment.getId() : null)
                .paymentStatus(payment != null && payment.getStatus() != null ? payment.getStatus().name() : null)
                .paymentTransactionCode(payment != null ? payment.getTransactionCode() : null)
                .message(message)
                .build();
    }
}
