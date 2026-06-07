package com.parking.system.entity;

import com.parking.system.enums.PaymentMethod;
import com.parking.system.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends BaseEntity {

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    @Column(length = 100)
    private String transactionCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_session_id", nullable = false, unique = true)
    private ParkingSession parkingSession;

    public void markPaid() {
        status = PaymentStatus.PAID;
        paidAt = LocalDateTime.now();
    }

    public void markFailed() {
        status = PaymentStatus.FAILED;
    }

    public void cancel() {
        status = PaymentStatus.CANCELLED;
    }

    public void refund() {
        status = PaymentStatus.REFUNDED;
    }
}
