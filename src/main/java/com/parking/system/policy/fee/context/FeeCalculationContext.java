package com.parking.system.policy.fee.context;

import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.PricingPolicy;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeeCalculationContext {
    private final ParkingSession parkingSession;
    private final PricingPolicy pricingPolicy;
    private final LocalDateTime calculationTime;
    private final boolean lostTicket;
    private final boolean wrongZone;

    public LocalDateTime getEffectiveEndTime() {
        if (parkingSession.getCheckOutTime() != null) {
            return parkingSession.getCheckOutTime();
        }
        if (calculationTime != null) {
            return calculationTime;
        }
        return LocalDateTime.now();
    }

    public long getBillableHours() {
        if (parkingSession.getCheckInTime() == null) {
            return 0L;
        }
        long minutes = ChronoUnit.MINUTES.between(parkingSession.getCheckInTime(), getEffectiveEndTime());
        if (minutes <= 0) {
            return 1L;
        }
        long hours = (minutes + 59) / 60;
        return Math.max(hours, 1L);
    }

    public long getOvernightCount() {
        if (parkingSession.getCheckInTime() == null) {
            return 0L;
        }
        long nights = ChronoUnit.DAYS.between(
                parkingSession.getCheckInTime().toLocalDate(),
                getEffectiveEndTime().toLocalDate());
        return Math.max(nights, 0L);
    }

    public boolean hasLostTicketCondition() {
        return lostTicket || parkingSession.getStatus() == com.parking.system.enums.SessionStatus.LOST_TICKET;
    }

    public boolean hasWrongZoneCondition() {
        return wrongZone || parkingSession.getStatus() == com.parking.system.enums.SessionStatus.WRONG_ZONE;
    }
}
