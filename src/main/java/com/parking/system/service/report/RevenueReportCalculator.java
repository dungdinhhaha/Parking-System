package com.parking.system.service.report;

import com.parking.system.dto.response.DailyRevenuePointResponse;
import com.parking.system.dto.response.RevenueReportResponse;
import com.parking.system.entity.Payment;
import com.parking.system.enums.PaymentStatus;
import com.parking.system.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RevenueReportCalculator {

    private final PaymentRepository paymentRepository;

    public RevenueReportCalculator(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public RevenueReportResponse calculate(LocalDateTime from, LocalDateTime to) {
        List<Payment> payments = paymentRepository.findAll();
        List<Payment> rangePayments = payments.stream()
                .filter(payment -> isWithin(payment.getCreatedAt(), from, to))
                .toList();

        BigDecimal totalRevenue = payments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.PAID)
                .filter(payment -> isWithin(payment.getPaidAt(), from, to))
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<LocalDate, List<Payment>> groupedByDate = payments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.PAID)
                .filter(payment -> payment.getPaidAt() != null)
                .filter(payment -> isWithin(payment.getPaidAt(), from, to))
                .sorted(Comparator.comparing(Payment::getPaidAt).reversed())
                .collect(Collectors.groupingBy(payment -> payment.getPaidAt().toLocalDate()));

        List<DailyRevenuePointResponse> dailyRevenue = groupedByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> DailyRevenuePointResponse.builder()
                        .date(entry.getKey())
                        .paymentCount(entry.getValue().size())
                        .revenue(entry.getValue().stream()
                                .map(Payment::getAmount)
                                .filter(amount -> amount != null)
                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                        .build())
                .toList();

        return RevenueReportResponse.builder()
                .from(from)
                .to(to)
                .totalRevenue(totalRevenue)
                .totalPayments(rangePayments.size())
                .paidPayments(countByStatus(rangePayments, PaymentStatus.PAID))
                .pendingPayments(countByStatus(rangePayments, PaymentStatus.PENDING))
                .failedPayments(countByStatus(rangePayments, PaymentStatus.FAILED))
                .cancelledPayments(countByStatus(rangePayments, PaymentStatus.CANCELLED))
                .refundedPayments(countByStatus(rangePayments, PaymentStatus.REFUNDED))
                .dailyRevenue(dailyRevenue)
                .build();
    }

    private long countByStatus(List<Payment> payments, PaymentStatus status) {
        return payments.stream().filter(payment -> payment.getStatus() == status).count();
    }

    private boolean isWithin(LocalDateTime value, LocalDateTime from, LocalDateTime to) {
        if (value == null) {
            return false;
        }
        return (from == null || !value.isBefore(from)) && (to == null || !value.isAfter(to));
    }
}
