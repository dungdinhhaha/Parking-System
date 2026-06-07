package com.parking.system.dto.response;

import com.parking.system.enums.IncidentType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IncidentCountResponse {
    private final IncidentType incidentType;
    private final long count;
}
