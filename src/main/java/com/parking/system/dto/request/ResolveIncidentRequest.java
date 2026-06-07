package com.parking.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResolveIncidentRequest {
    @NotBlank
    private String resolutionNote;
}
