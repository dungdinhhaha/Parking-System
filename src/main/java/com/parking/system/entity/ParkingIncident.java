package com.parking.system.entity;

import com.parking.system.enums.IncidentStatus;
import com.parking.system.enums.IncidentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "parking_incidents")
@Getter
@Setter
public class ParkingIncident extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private IncidentType incidentType;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private IncidentStatus status;

    private LocalDateTime resolvedAt;

    @Column(length = 1000)
    private String resolutionNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_session_id")
    private ParkingSession parkingSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_id")
    private User reportedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id")
    private User resolvedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plate_recognition_log_id")
    private PlateRecognitionLog plateRecognitionLog;

    public void resolve(String note) {
        resolutionNote = note;
        resolvedAt = LocalDateTime.now();
        status = IncidentStatus.RESOLVED;
    }

    public void cancel() {
        status = IncidentStatus.CANCELLED;
    }

    public void markProcessing() {
        status = IncidentStatus.PROCESSING;
    }
}
