package com.parking.system.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyRevenuePointResponse {
    private final LocalDate date;
    private final BigDecimal revenue;
    private final long paymentCount;
}
