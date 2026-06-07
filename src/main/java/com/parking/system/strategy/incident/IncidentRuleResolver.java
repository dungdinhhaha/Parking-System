package com.parking.system.strategy.incident;

import com.parking.system.enums.IncidentType;
import com.parking.system.exception.BusinessException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IncidentRuleResolver {
    private final List<IncidentRule> rules;

    public IncidentRuleResolver(List<IncidentRule> rules) {
        this.rules = rules;
    }

    public IncidentRule resolve(IncidentType incidentType) {
        return rules.stream()
                .filter(rule -> rule.supports(incidentType))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Unsupported incident type: " + incidentType));
    }
}
