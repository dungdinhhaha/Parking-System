package com.parking.system.service.operations;

import com.parking.system.entity.ParkingSession;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class OperationsRealtimeService {

    public SseEmitter subscribe() {
        return new SseEmitter(0L);
    }

    public void publishCheckInSuccess(String buildingCode, ParkingSession session, Object allocationSource, long durationMs) {
    }

    public void publishCheckInSuccess(ParkingSession session, Object allocationSource, long durationMs) {
    }

    public void publishCheckOutSuccess(ParkingSession session, Object payment, BigDecimal amount) {
    }

    public void publishWarning(String source, String title, String message, String buildingCode, Long referenceId) {
    }
}
