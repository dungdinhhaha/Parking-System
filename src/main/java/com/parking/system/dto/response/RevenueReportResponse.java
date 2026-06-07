package com.parking.system.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RevenueReportResponse {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final BigDecimal totalRevenue;
    private final long totalPayments;
    private final long paidPayments;
    private final long pendingPayments;
    private final long failedPayments;
    private final long cancelledPayments;
    private final long refundedPayments;
    private final List<DailyRevenuePointResponse> dailyRevenue;
}
