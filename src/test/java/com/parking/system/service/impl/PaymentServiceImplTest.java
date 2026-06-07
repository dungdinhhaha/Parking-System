package com.parking.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parking.system.dto.request.CreatePaymentRequest;
import com.parking.system.dto.response.FeeCalculationResponse;
import com.parking.system.dto.response.PaymentResponse;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.Payment;
import com.parking.system.enums.PaymentMethod;
import com.parking.system.enums.PaymentStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.processor.CashPaymentProcessor;
import com.parking.system.processor.PaymentProcessor;
import com.parking.system.processor.PaymentProcessorFactory;
import com.parking.system.processor.payment.PaymentProcessingContext;
import com.parking.system.processor.payment.PaymentProcessingResult;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.PaymentRepository;
import com.parking.system.service.FeeCalculationService;
import com.parking.system.support.TestFixtures;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private ParkingSessionRepository parkingSessionRepository;
    @Mock private FeeCalculationService feeCalculationService;
    @Mock private PaymentProcessorFactory paymentProcessorFactory;
    @Mock private PaymentProcessor paymentProcessor;

    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository, parkingSessionRepository, feeCalculationService, paymentProcessorFactory);
    }

    @Test
    void create_cashPayment_success_marksPaid() {
        ParkingSession session = TestFixtures.activeSession(TestFixtures.user("staff", com.parking.system.enums.UserRole.STAFF),
                TestFixtures.vehicle("51A-123.45", VehicleType.CAR, TestFixtures.user("staff2", com.parking.system.enums.UserRole.STAFF)),
                TestFixtures.carZone(200L, "B1-CAR", "BLD-001"),
                TestFixtures.slot(300L, "C1", TestFixtures.carZone(200L, "B1-CAR", "BLD-001"), com.parking.system.enums.SlotStatus.OCCUPIED));
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setParkingSessionId(session.getId());
        request.setMethod(PaymentMethod.CASH);
        request.setCalculationTime(LocalDateTime.now());

        when(parkingSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(paymentRepository.findByParkingSession_Id(session.getId())).thenReturn(Optional.empty());
        when(feeCalculationService.calculate(any())).thenReturn(FeeCalculationResponse.builder()
                .parkingSessionId(session.getId())
                .ticketCode(session.getTicketCode())
                .plateNumber(session.getPlateNumber())
                .vehicleType(session.getVehicleType())
                .calculationTime(LocalDateTime.now())
                .subtotal(BigDecimal.valueOf(15000))
                .totalAmount(BigDecimal.valueOf(15000))
                .items(List.of())
                .build());
        when(paymentProcessorFactory.getProcessor(PaymentMethod.CASH)).thenReturn(paymentProcessor);
        when(paymentProcessor.process(any(PaymentProcessingContext.class))).thenReturn(PaymentProcessingResult.builder()
                .success(true)
                .provider("CASH")
                .transactionCode("CASH-TEST")
                .message("OK")
                .build());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            if (payment.getId() == null) {
                payment.setId(123L);
            }
            return payment;
        });
        when(parkingSessionRepository.save(any(ParkingSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.create(request);

        assertEquals(PaymentStatus.PAID, response.getStatus());
        assertEquals(BigDecimal.valueOf(15000), response.getAmount());
        assertEquals("CASH-TEST", response.getTransactionCode());
        assertNotNull(response.getPaidAt());
        verify(paymentProcessorFactory).getProcessor(PaymentMethod.CASH);
        verify(parkingSessionRepository).save(session);
    }

    @Test
    void create_paymentFails_marksFailed() {
        ParkingSession session = TestFixtures.activeSession(TestFixtures.user("staff", com.parking.system.enums.UserRole.STAFF),
                TestFixtures.vehicle("51A-123.45", VehicleType.CAR, TestFixtures.user("staff2", com.parking.system.enums.UserRole.STAFF)),
                TestFixtures.carZone(200L, "B1-CAR", "BLD-001"),
                TestFixtures.slot(300L, "C1", TestFixtures.carZone(200L, "B1-CAR", "BLD-001"), com.parking.system.enums.SlotStatus.OCCUPIED));
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setParkingSessionId(session.getId());
        request.setMethod(PaymentMethod.CASH);
        request.setCalculationTime(LocalDateTime.now());

        when(parkingSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(paymentRepository.findByParkingSession_Id(session.getId())).thenReturn(Optional.empty());
        when(feeCalculationService.calculate(any())).thenReturn(FeeCalculationResponse.builder()
                .parkingSessionId(session.getId())
                .ticketCode(session.getTicketCode())
                .plateNumber(session.getPlateNumber())
                .vehicleType(session.getVehicleType())
                .calculationTime(LocalDateTime.now())
                .subtotal(BigDecimal.valueOf(15000))
                .totalAmount(BigDecimal.valueOf(15000))
                .items(List.of())
                .build());
        when(paymentProcessorFactory.getProcessor(PaymentMethod.CASH)).thenReturn(paymentProcessor);
        when(paymentProcessor.process(any(PaymentProcessingContext.class))).thenReturn(PaymentProcessingResult.builder()
                .success(false)
                .provider("CASH")
                .transactionCode(null)
                .message("FAILED")
                .build());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            if (payment.getId() == null) {
                payment.setId(124L);
            }
            return payment;
        });
        when(parkingSessionRepository.save(any(ParkingSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.create(request);

        assertEquals(PaymentStatus.FAILED, response.getStatus());
        assertEquals(BigDecimal.valueOf(15000), response.getAmount());
        assertEquals(null, response.getTransactionCode());
        verify(parkingSessionRepository).save(session);
    }
}
