package com.parking.system.strategy.incident.impl;

import com.parking.system.entity.ParkingSession;
import com.parking.system.enums.IncidentType;
import com.parking.system.strategy.incident.IncidentRule;
import com.parking.system.strategy.incident.IncidentRuleContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(40)
public class WrongZoneIncidentRule implements IncidentRule {

    @Override
    public boolean supports(IncidentType incidentType) {
        return incidentType == IncidentType.WRONG_ZONE;
    }

    @Override
    public void apply(IncidentRuleContext context) {
        ParkingSession session = context.getSession();
        session.markWrongZone();
    }
}
