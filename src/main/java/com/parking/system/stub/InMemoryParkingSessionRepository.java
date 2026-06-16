package com.parking.system.stub;

import com.parking.system.entity.ParkingSession;
import com.parking.system.enums.SessionStatus;
import com.parking.system.repository.ParkingSessionRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class InMemoryParkingSessionRepository implements ParkingSessionRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, ParkingSession> sessions = new LinkedHashMap<>();

    @Override
    public Optional<ParkingSession> findByCheckInRequestId(String requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<ParkingSession> findByCheckOutRequestId(String requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<ParkingSession> findDetailedFirstByRfidCardIdIgnoreCaseAndStatusOrderByCreatedAtDesc(String rfidCardId, SessionStatus status) {
        return sessions.values().stream()
                .filter(session -> session.getRfidCardId() != null && session.getRfidCardId().equalsIgnoreCase(rfidCardId))
                .filter(session -> session.getStatus() == status)
                .findFirst();
    }

    @Override
    public Optional<ParkingSession> findDetailedById(Long id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public Optional<ParkingSession> findDetailedByIdForUpdate(Long id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public boolean existsByPlateNumberIgnoreCaseAndStatus(String plateNumber, SessionStatus status) {
        return sessions.values().stream()
                .anyMatch(session -> session.getPlateNumber() != null
                        && session.getPlateNumber().equalsIgnoreCase(plateNumber)
                        && session.getStatus() == status);
    }

    @Override
    public boolean existsByRfidCardIdIgnoreCaseAndStatus(String rfidCardId, SessionStatus status) {
        return sessions.values().stream()
                .anyMatch(session -> session.getRfidCardId() != null
                        && session.getRfidCardId().equalsIgnoreCase(rfidCardId)
                        && session.getStatus() == status);
    }

    @Override
    public ParkingSession save(ParkingSession session) {
        if (session.getId() == null) {
            session.setId(sequence.getAndIncrement());
        }
        sessions.put(session.getId(), session);
        return session;
    }
}
