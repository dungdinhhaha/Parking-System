package com.parking.system.entity;

import com.parking.system.enums.RecognitionStatus;
import com.parking.system.enums.RecognitionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "plate_recognition_logs")
@Getter
@Setter
public class PlateRecognitionLog extends BaseEntity {

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 255)
    private String originalFileName;

    @Column(length = 30)
    private String detectedPlateNumber;

    private Double confidence;

    @Column(length = 100)
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private RecognitionType recognitionType;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private RecognitionStatus status;

    @Column(length = 30)
    private String confirmedPlateNumber;

    private Boolean isConfirmed = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_session_id")
    private ParkingSession parkingSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;

    @OneToMany(mappedBy = "plateRecognitionLog", fetch = FetchType.LAZY)
    private List<ParkingIncident> incidents = new ArrayList<>();

    public void confirmPlate(String plateNumber) {
        confirmedPlateNumber = plateNumber;
        isConfirmed = Boolean.TRUE;
        status = RecognitionStatus.SUCCESS;
    }

    public void markFailed() {
        isConfirmed = Boolean.FALSE;
        status = RecognitionStatus.FAILED;
    }

    public void markLowConfidence() {
        isConfirmed = Boolean.FALSE;
        status = RecognitionStatus.LOW_CONFIDENCE;
    }

    public void markManualCorrected() {
        isConfirmed = Boolean.TRUE;
        status = RecognitionStatus.MANUAL_CORRECTED;
    }
}
