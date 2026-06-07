package com.parking.system.facade;

import com.parking.system.dto.request.CreateIncidentRequest;
import com.parking.system.dto.request.ResolveIncidentRequest;
import com.parking.system.dto.response.IncidentResponse;
import com.parking.system.entity.ParkingIncident;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.PlateRecognitionLog;
import com.parking.system.entity.User;
import com.parking.system.enums.IncidentStatus;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingIncidentRepository;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.PlateRecognitionLogRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.service.incident.IncidentMapper;
import com.parking.system.service.incident.IncidentValidator;
import com.parking.system.strategy.incident.IncidentRule;
import com.parking.system.strategy.incident.IncidentRuleContext;
import com.parking.system.strategy.incident.IncidentRuleResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IncidentFacade {

    private final ParkingIncidentRepository parkingIncidentRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final PlateRecognitionLogRepository plateRecognitionLogRepository;
    private final UserRepository userRepository;
    private final IncidentValidator validator;
    private final IncidentRuleResolver ruleResolver;
    private final IncidentMapper mapper;

    @Transactional
    public IncidentResponse create(String username, CreateIncidentRequest request) {
        validator.validateCreateRequest(request);
        User reporter = getUser(username);
        ParkingSession session = getSession(request.getParkingSessionId());
        PlateRecognitionLog plateRecognitionLog = getPlateRecognitionLog(request.getPlateRecognitionLogId(), session);

        ParkingIncident incident = new ParkingIncident();
        incident.setIncidentType(request.getIncidentType());
        incident.setDescription(request.getDescription().trim());
        incident.setStatus(IncidentStatus.OPEN);
        incident.setParkingSession(session);
        incident.setReportedBy(reporter);
        incident.setPlateRecognitionLog(plateRecognitionLog);

        ParkingIncident saved = parkingIncidentRepository.save(incident);
        IncidentRule rule = ruleResolver.resolve(request.getIncidentType());
        rule.apply(IncidentRuleContext.builder()
                .request(request)
                .incident(saved)
                .session(session)
                .reporter(reporter)
                .plateRecognitionLog(plateRecognitionLog)
                .build());

        parkingSessionRepository.save(session);
        return mapper.toResponse(parkingIncidentRepository.save(saved));
    }

    @Transactional
    public IncidentResponse close(String username, Long incidentId, ResolveIncidentRequest request) {
        validator.validateCloseRequest(request);
        ParkingIncident incident = getIncident(incidentId);
        if (incident.getStatus() == IncidentStatus.RESOLVED || incident.getStatus() == IncidentStatus.CANCELLED) {
            return mapper.toResponse(incident);
        }

        User resolver = getUser(username);
        incident.setResolvedBy(resolver);
        incident.resolve(request.getResolutionNote().trim());
        return mapper.toResponse(parkingIncidentRepository.save(incident));
    }

    @Transactional(readOnly = true)
    public IncidentResponse get(Long incidentId) {
        return mapper.toResponse(getIncident(incidentId));
    }

    @Transactional(readOnly = true)
    public List<IncidentResponse> getAll() {
        return mapper.toResponses(parkingIncidentRepository.findAllByOrderByCreatedAtDesc());
    }

    @Transactional(readOnly = true)
    public List<IncidentResponse> getBySession(Long parkingSessionId) {
        return mapper.toResponses(parkingIncidentRepository.findAllByParkingSession_IdOrderByCreatedAtDesc(parkingSessionId));
    }

    @Transactional(readOnly = true)
    public List<IncidentResponse> getByStatus(IncidentStatus status) {
        if (status == null) {
            return getAll();
        }
        return mapper.toResponses(parkingIncidentRepository.findAllByStatusOrderByCreatedAtDesc(status));
    }

    private ParkingIncident getIncident(Long incidentId) {
        return parkingIncidentRepository.findById(incidentId)
                .orElseThrow(() -> new BusinessException("Incident not found"));
    }

    private ParkingSession getSession(Long parkingSessionId) {
        return parkingSessionRepository.findById(parkingSessionId)
                .orElseThrow(() -> new BusinessException("Parking session not found"));
    }

    private PlateRecognitionLog getPlateRecognitionLog(Long plateRecognitionLogId, ParkingSession session) {
        if (plateRecognitionLogId == null) {
            return null;
        }
        PlateRecognitionLog plateRecognitionLog = plateRecognitionLogRepository.findById(plateRecognitionLogId)
                .orElseThrow(() -> new BusinessException("Plate recognition log not found"));
        if (plateRecognitionLog.getParkingSession() != null
                && plateRecognitionLog.getParkingSession().getId() != null
                && !plateRecognitionLog.getParkingSession().getId().equals(session.getId())) {
            throw new BusinessException("Plate recognition log does not belong to the parking session");
        }
        return plateRecognitionLog;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));
    }
}
