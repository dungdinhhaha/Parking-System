package com.parking.system.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parking.system.dto.request.CheckOutRequest;
import com.parking.system.dto.response.CheckOutResponse;
import com.parking.system.dto.response.PaymentResponse;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.Payment;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.enums.PaymentMethod;
import com.parking.system.enums.PaymentStatus;
import com.parking.system.enums.SessionStatus;
import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.UserRole;
import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.PaymentRepository;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.service.FeeCalculationService;
import com.parking.system.service.PaymentService;
import com.parking.system.service.checkout.CheckOutMapper;
import com.parking.system.service.checkout.CheckOutPlateResolver;
import com.parking.system.service.checkout.CheckOutResourceReleaser;
import com.parking.system.service.checkout.CheckOutSessionCloser;
import com.parking.system.service.checkout.CheckOutValidator;
import com.parking.system.support.TestFixtures;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckOutFacadeTest {

    @Mock private ParkingSessionRepository parkingSessionRepository;
    @Mock private UserRepository userRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private FeeCalculationService feeCalculationService;
    @Mock private PaymentService paymentService;
    @Mock private CheckOutPlateResolver checkOutPlateResolver;
    @Mock private ParkingSlotRepository parkingSlotRepository;
    @Mock private ParkingZoneRepository parkingZoneRepository;

    private CheckOutFacade facade;

    @BeforeEach
    void setUp() {
        facade = new CheckOutFacade(
                parkingSessionRepository,
                userRepository,
                paymentRepository,
                feeCalculationService,
                paymentService,
                new CheckOutValidator(),
                checkOutPlateResolver,
                new CheckOutResourceReleaser(parkingSlotRepository, parkingZoneRepository),
                new CheckOutSessionCloser(),
                new CheckOutMapper());
    }

    @Test
    void checkOut_success_completesSessionAndReleasesSlot() {
        User staff = TestFixtures.user("staff", UserRole.STAFF);
        ParkingZone zone = TestFixtures.carZone(200L, "B1-CAR", "BLD-001");
        ParkingSlot slot = TestFixtures.slot(300L, "C1", zone, SlotStatus.OCCUPIED);
        Vehicle vehicle = TestFixtures.vehicle("51A-123.45", VehicleType.CAR, staff);
        ParkingSession session = TestFixtures.activeSession(staff, vehicle, zone, slot);
        Payment payment = TestFixtures.paidCashPayment(session, BigDecimal.valueOf(15000));

        CheckOutRequest request = checkOutRequest(session.getId(), vehicle.getPlateNumber());

        when(parkingSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(userRepository.findByUsername("staff")).thenReturn(Optional.of(staff));
        when(checkOutPlateResolver.resolve(session, staff, null, vehicle.getPlateNumber())).thenReturn(vehicle.getPlateNumber());
        when(feeCalculationService.calculate(any())).thenReturn(feeResponse(session, BigDecimal.valueOf(15000)));
        when(paymentService.create(any())).thenReturn(paymentResponsePaid(session.getId(), BigDecimal.valueOf(15000)));
        when(paymentRepository.findByParkingSession_Id(session.getId())).thenReturn(Optional.of(payment));
        when(parkingSlotRepository.save(any(ParkingSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(parkingSessionRepository.save(any(ParkingSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CheckOutResponse response = facade.checkOut("staff", request);

        assertEquals(session.getId(), response.getParkingSessionId());
        assertEquals(SessionStatus.COMPLETED, session.getStatus());
        assertEquals(vehicle.getPlateNumber(), response.getPlateNumber());
        assertEquals(BigDecimal.valueOf(15000), response.getFeeAmount());
        assertEquals(payment.getId(), response.getPaymentId());
        assertEquals(PaymentStatus.PAID.name(), response.getPaymentStatus());
        assertNotNull(response.getCheckOutTime());
        assertEquals(SlotStatus.AVAILABLE, slot.getStatus());
        verify(parkingSlotRepository).save(slot);
    }

    @Test
    void checkOut_paymentFailed_throwsBusinessException() {
        User staff = TestFixtures.user("staff", UserRole.STAFF);
        ParkingZone zone = TestFixtures.carZone(200L, "B1-CAR", "BLD-001");
        ParkingSlot slot = TestFixtures.slot(300L, "C1", zone, SlotStatus.OCCUPIED);
        Vehicle vehicle = TestFixtures.vehicle("51A-123.45", VehicleType.CAR, staff);
        ParkingSession session = TestFixtures.activeSession(staff, vehicle, zone, slot);
        Payment payment = TestFixtures.failedCashPayment(session, BigDecimal.valueOf(15000));

        CheckOutRequest request = checkOutRequest(session.getId(), vehicle.getPlateNumber());

        when(parkingSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(userRepository.findByUsername("staff")).thenReturn(Optional.of(staff));
        when(checkOutPlateResolver.resolve(session, staff, null, vehicle.getPlateNumber())).thenReturn(vehicle.getPlateNumber());
        when(feeCalculationService.calculate(any())).thenReturn(feeResponse(session, BigDecimal.valueOf(15000)));
        when(paymentService.create(any())).thenReturn(paymentResponseFailed(session.getId(), BigDecimal.valueOf(15000)));
        when(paymentRepository.findByParkingSession_Id(session.getId())).thenReturn(Optional.of(payment));

        BusinessException ex = assertThrows(BusinessException.class, () -> facade.checkOut("staff", request));
        assertEquals("Payment failed", ex.getMessage());
        assertEquals(SessionStatus.ACTIVE, session.getStatus());
    }

    @Test
    void checkOut_wrongPlate_throwsBusinessException() {
        User staff = TestFixtures.user("staff", UserRole.STAFF);
        ParkingZone zone = TestFixtures.carZone(200L, "B1-CAR", "BLD-001");
        ParkingSlot slot = TestFixtures.slot(300L, "C1", zone, SlotStatus.OCCUPIED);
        Vehicle vehicle = TestFixtures.vehicle("51A-123.45", VehicleType.CAR, staff);
        ParkingSession session = TestFixtures.activeSession(staff, vehicle, zone, slot);

        CheckOutRequest request = checkOutRequest(session.getId(), vehicle.getPlateNumber());

        when(parkingSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(userRepository.findByUsername("staff")).thenReturn(Optional.of(staff));
        when(checkOutPlateResolver.resolve(session, staff, null, vehicle.getPlateNumber())).thenReturn("52B-999.99");

        BusinessException ex = assertThrows(BusinessException.class, () -> facade.checkOut("staff", request));
        assertEquals("Plate number does not match active session", ex.getMessage());
        assertEquals(SessionStatus.WRONG_PLATE, session.getStatus());
    }

    private CheckOutRequest checkOutRequest(Long sessionId, String plateNumber) {
        CheckOutRequest request = new CheckOutRequest();
        request.setParkingSessionId(sessionId);
        request.setPlateNumber(plateNumber);
        request.setExitGate("Gate B");
        request.setPaymentMethod(PaymentMethod.CASH);
        request.setCalculationTime(LocalDateTime.now());
        request.setLostTicket(Boolean.FALSE);
        request.setWrongZone(Boolean.FALSE);
        return request;
    }

    private com.parking.system.dto.response.FeeCalculationResponse feeResponse(ParkingSession session, BigDecimal amount) {
        return com.parking.system.dto.response.FeeCalculationResponse.builder()
                .parkingSessionId(session.getId())
                .ticketCode(session.getTicketCode())
                .plateNumber(session.getPlateNumber())
                .vehicleType(session.getVehicleType())
                .checkInTime(session.getCheckInTime())
                .calculationTime(LocalDateTime.now())
                .subtotal(amount)
                .totalAmount(amount)
                .items(java.util.List.of())
                .build();
    }

    private PaymentResponse paymentResponsePaid(Long sessionId, BigDecimal amount) {
        return PaymentResponse.builder()
                .id(123L)
                .parkingSessionId(sessionId)
                .amount(amount)
                .method(PaymentMethod.CASH)
                .status(PaymentStatus.PAID)
                .transactionCode("CASH-TEST")
                .paidAt(LocalDateTime.now())
                .build();
    }

    private PaymentResponse paymentResponseFailed(Long sessionId, BigDecimal amount) {
        return PaymentResponse.builder()
                .id(124L)
                .parkingSessionId(sessionId)
                .amount(amount)
                .method(PaymentMethod.CASH)
                .status(PaymentStatus.FAILED)
                .transactionCode(null)
                .paidAt(null)
                .build();
    }
}
