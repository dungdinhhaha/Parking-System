package com.parking.system.dto.request;

import com.parking.system.enums.IncidentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateIncidentRequest {
    @NotNull
    private Long parkingSessionId;

    @NotNull
    private IncidentType incidentType;

    @NotBlank
    private String description;

    private Long plateRecognitionLogId;

    private String resolutionNote;
}
