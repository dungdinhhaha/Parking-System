package com.parking.system.service.checkout;

import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.Payment;
import com.parking.system.enums.SessionStatus;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CheckOutCompletionService {

    private final ParkingSessionRepository parkingSessionRepository;
    private final CheckOutSessionCloser sessionCloser;
    private final CheckOutResourceReleaser resourceReleaser;

    @Transactional
    public ParkingSession complete(Long sessionId, String requestId, String exitGate, Payment payment) {
        ParkingSession session = parkingSessionRepository.findDetailedByIdForUpdate(sessionId)
                .orElseThrow(() -> new BusinessException("Parking session not found"));
        if (session.getStatus() == SessionStatus.COMPLETED) {
            return session;
        }
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new BusinessException("Parking session is not active");
        }

        session.setCheckOutRequestId(normalizeRequestId(requestId));
        sessionCloser.close(session, exitGate);
        resourceReleaser.release(session);
        return parkingSessionRepository.save(session);
    }

    private String normalizeRequestId(String requestId) {
        return requestId == null || requestId.isBlank() ? null : requestId.trim();
    }
}
