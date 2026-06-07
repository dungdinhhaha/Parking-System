package com.parking.system.strategy.incident;

import com.parking.system.enums.IncidentType;

public interface IncidentRule {
    boolean supports(IncidentType incidentType);
    void apply(IncidentRuleContext context);
}
