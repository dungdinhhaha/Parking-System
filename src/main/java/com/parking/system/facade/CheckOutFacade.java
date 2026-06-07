package com.parking.system.facade;

import com.parking.system.dto.request.CheckOutRequest;
import com.parking.system.dto.response.CheckOutResponse;
import com.parking.system.dto.response.PaymentResponse;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.Payment;
import com.parking.system.entity.User;
import com.parking.system.enums.PaymentMethod;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.repository.PaymentRepository;
import com.parking.system.service.FeeCalculationService;
import com.parking.system.service.PaymentService;
import com.parking.system.service.checkout.CheckOutMapper;
import com.parking.system.service.checkout.CheckOutPlateResolver;
import com.parking.system.service.checkout.CheckOutResourceReleaser;
import com.parking.system.service.checkout.CheckOutSessionCloser;
import com.parking.system.service.checkout.CheckOutValidator;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CheckOutFacade {

    private final ParkingSessionRepository parkingSessionRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final FeeCalculationService feeCalculationService;
    private final PaymentService paymentService;
    private final CheckOutValidator checkOutValidator;
    private final CheckOutPlateResolver checkOutPlateResolver;
    private final CheckOutResourceReleaser checkOutResourceReleaser;
    private final CheckOutSessionCloser checkOutSessionCloser;
    private final CheckOutMapper checkOutMapper;

    @Transactional
    public CheckOutResponse checkOut(String username, CheckOutRequest request) {
        ParkingSession session = parkingSessionRepository.findById(request.getParkingSessionId())
                .orElseThrow(() -> new BusinessException("Parking session not found"));
        checkOutValidator.validate(session, request);

        User staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));

        String resolvedPlate = checkOutPlateResolver.resolve(
                session,
                staff,
                request.getPlateImage(),
                request.getPlateNumber());

        if (resolvedPlate != null && !resolvedPlate.equalsIgnoreCase(session.getPlateNumber())) {
            session.markWrongPlate();
            parkingSessionRepository.save(session);
            throw new BusinessException("Plate number does not match active session");
        }

        BigDecimal feeAmount = feeCalculationService.calculate(buildFeeRequest(request)).getTotalAmount();
        PaymentResponse paymentResponse = paymentService.create(buildPaymentRequest(request));
        Payment payment = paymentRepository.findByParkingSession_Id(session.getId())
                .orElseThrow(() -> new BusinessException("Payment not found after processing"));

        if (paymentResponse.getStatus() == null || !paymentResponse.getStatus().name().equals("PAID")) {
            throw new BusinessException("Payment failed");
        }

        checkOutSessionCloser.close(session, request.getExitGate());
        checkOutResourceReleaser.release(session);
        session.setPayment(payment);
        parkingSessionRepository.save(session);

        return checkOutMapper.map(session, payment, feeAmount, "Check-out completed successfully");
    }

    private com.parking.system.dto.request.CalculateFeeRequest buildFeeRequest(CheckOutRequest request) {
        com.parking.system.dto.request.CalculateFeeRequest feeRequest = new com.parking.system.dto.request.CalculateFeeRequest();
        feeRequest.setParkingSessionId(request.getParkingSessionId());
        feeRequest.setCalculationTime(request.getCalculationTime());
        feeRequest.setLostTicket(request.getLostTicket());
        feeRequest.setWrongZone(request.getWrongZone());
        return feeRequest;
    }

    private com.parking.system.dto.request.CreatePaymentRequest buildPaymentRequest(CheckOutRequest request) {
        com.parking.system.dto.request.CreatePaymentRequest paymentRequest = new com.parking.system.dto.request.CreatePaymentRequest();
        paymentRequest.setParkingSessionId(request.getParkingSessionId());
        paymentRequest.setMethod(request.getPaymentMethod() == null ? PaymentMethod.CASH : request.getPaymentMethod());
        paymentRequest.setCalculationTime(request.getCalculationTime());
        paymentRequest.setLostTicket(request.getLostTicket());
        paymentRequest.setWrongZone(request.getWrongZone());
        return paymentRequest;
    }
}
