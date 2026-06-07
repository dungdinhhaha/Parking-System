package com.parking.system.entity;

import com.parking.system.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "vehicles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vehicles_plate_number", columnNames = "plateNumber")
        }
)
@Getter
@Setter
public class Vehicle extends BaseEntity {

    @Column(name = "plate_number", nullable = false, length = 30)
    private String plateNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VehicleType vehicleType;

    @Column(length = 100)
    private String color;

    @Column(length = 100)
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private List<ParkingSession> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private List<PlateRecognitionLog> recognitionLogs = new ArrayList<>();
}
