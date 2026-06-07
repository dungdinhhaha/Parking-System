package com.parking.system.service.checkout;

import com.parking.system.entity.ParkingSession;
import com.parking.system.enums.SessionStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class CheckOutSessionCloser {

    public void close(ParkingSession session, String exitGate) {
        session.setExitGate(exitGate);
        session.setCheckOutTime(LocalDateTime.now());
        session.setStatus(SessionStatus.COMPLETED);
    }
}
