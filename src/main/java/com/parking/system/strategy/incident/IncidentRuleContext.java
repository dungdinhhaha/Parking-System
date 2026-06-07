package com.parking.system.strategy.incident;

import com.parking.system.dto.request.CreateIncidentRequest;
import com.parking.system.entity.ParkingIncident;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.PlateRecognitionLog;
import com.parking.system.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IncidentRuleContext {
    private final CreateIncidentRequest request;
    private final ParkingIncident incident;
    private final ParkingSession session;
    private final User reporter;
    private final PlateRecognitionLog plateRecognitionLog;
}
