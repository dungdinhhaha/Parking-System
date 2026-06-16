package com.parking.system.entity;

import com.parking.system.enums.IncidentStatus;
import com.parking.system.enums.IncidentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingIncident extends BaseEntity {
    private IncidentType incidentType;
    private IncidentStatus status;
    private String description;
    private ParkingSession parkingSession;
    private User reportedBy;
}
