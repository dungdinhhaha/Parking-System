package com.parking.system.entity;

import com.parking.system.enums.RecognitionStatus;
import com.parking.system.enums.RecognitionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlateRecognitionLog extends BaseEntity {
    private String imageUrl;
    private String originalFileName;
    private String detectedPlateNumber;
    private Double confidence;
    private String provider;
    private RecognitionType recognitionType;
    private RecognitionStatus status;
    private String confirmedPlateNumber;
    private Boolean isConfirmed;
    private User uploadedBy;
    private ParkingSession parkingSession;
    private Vehicle vehicle;

    public void confirmPlate(String plateNumber) {
        this.confirmedPlateNumber = plateNumber;
        this.isConfirmed = Boolean.TRUE;
    }
}
