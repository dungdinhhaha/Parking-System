package com.parking.system.service.impl;

import com.parking.system.dto.request.CalculateFeeRequest;
import com.parking.system.dto.request.CreatePaymentRequest;
import com.parking.system.dto.response.FeeCalculationResponse;
import com.parking.system.dto.response.PaymentResponse;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.Payment;
import com.parking.system.enums.PaymentStatus;
import com.parking.system.exception.BusinessException;
import com.parking.system.processor.PaymentProcessor;
import com.parking.system.processor.PaymentProcessorFactory;
import com.parking.system.processor.payment.PaymentProcessingContext;
import com.parking.system.processor.payment.PaymentProcessingResult;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.PaymentRepository;
import com.parking.system.service.FeeCalculationService;
import com.parking.system.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final FeeCalculationService feeCalculationService;
    private final PaymentProcessorFactory paymentProcessorFactory;

    @Override
    @Transactional
    public PaymentResponse create(CreatePaymentRequest request) {
        ParkingSession session = parkingSessionRepository.findById(request.getParkingSessionId())
                .orElseThrow(() -> new BusinessException("Parking session not found"));

        Payment existing = paymentRepository.findByParkingSession_Id(session.getId()).orElse(null);
        if (existing != null && existing.getStatus() == PaymentStatus.PAID) {
            return map(existing);
        }

        FeeCalculationResponse feeCalculation = feeCalculationService.calculate(buildFeeRequest(request));

        Payment payment = existing != null ? existing : new Payment();
        payment.setParkingSession(session);
        payment.setAmount(feeCalculation.getTotalAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        PaymentProcessor processor = paymentProcessorFactory.getProcessor(request.getMethod());
        PaymentProcessingResult result = processor.process(PaymentProcessingContext.builder()
                .parkingSession(session)
                .payment(payment)
                .amount(feeCalculation.getTotalAmount())
                .build());

        if (result.isSuccess()) {
            payment.markPaid();
            payment.setTransactionCode(result.getTransactionCode());
            session.setPayment(payment);
        } else {
            payment.markFailed();
        }

        payment = paymentRepository.save(payment);
        parkingSessionRepository.save(session);
        return map(payment);
    }

    @Override
    public PaymentResponse get(Long id) {
        return map(paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Payment not found")));
    }

    @Override
    public PaymentResponse getByParkingSessionId(Long parkingSessionId) {
        return map(paymentRepository.findByParkingSession_Id(parkingSessionId)
                .orElseThrow(() -> new BusinessException("Payment not found for parking session")));
    }

    @Override
    public List<PaymentResponse> getAll() {
        return paymentRepository.findAllByOrderByCreatedAtDesc().stream().map(this::map).toList();
    }

    private CalculateFeeRequest buildFeeRequest(CreatePaymentRequest request) {
        CalculateFeeRequest feeRequest = new CalculateFeeRequest();
        feeRequest.setParkingSessionId(request.getParkingSessionId());
        feeRequest.setCalculationTime(request.getCalculationTime());
        feeRequest.setLostTicket(request.getLostTicket());
        feeRequest.setWrongZone(request.getWrongZone());
        return feeRequest;
    }

    private PaymentResponse map(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .parkingSessionId(payment.getParkingSession() != null ? payment.getParkingSession().getId() : null)
                .ticketCode(payment.getParkingSession() != null ? payment.getParkingSession().getTicketCode() : null)
                .plateNumber(payment.getParkingSession() != null ? payment.getParkingSession().getPlateNumber() : null)
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionCode(payment.getTransactionCode())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
