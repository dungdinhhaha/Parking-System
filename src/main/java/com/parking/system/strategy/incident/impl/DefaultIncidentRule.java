package com.parking.system.strategy.incident.impl;

import com.parking.system.enums.IncidentType;
import com.parking.system.strategy.incident.IncidentRule;
import com.parking.system.strategy.incident.IncidentRuleContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
public class DefaultIncidentRule implements IncidentRule {

    @Override
    public boolean supports(IncidentType incidentType) {
        return incidentType == IncidentType.OTHER || incidentType == IncidentType.OCCUPIED_RESERVED_SLOT;
    }

    @Override
    public void apply(IncidentRuleContext context) {
        // No session state change for generic incidents.
    }
}
