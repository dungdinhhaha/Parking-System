package com.parking.system.entity;

import com.parking.system.enums.BuildingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "parking_buildings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_parking_buildings_building_code", columnNames = "building_code")
        }
)
@Getter
@Setter
public class ParkingBuilding extends BaseEntity {

    @Column(name = "building_code", nullable = false, length = 50)
    private String buildingCode;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String address;

    private LocalTime openTime;

    private LocalTime closeTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private BuildingStatus status;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY)
    private List<ParkingFloor> floors = new ArrayList<>();
}
