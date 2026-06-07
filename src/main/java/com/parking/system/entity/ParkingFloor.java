package com.parking.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "parking_floors",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_parking_floors_building_floor_code", columnNames = {"building_id", "floor_code"})
        }
)
@Getter
@Setter
public class ParkingFloor extends BaseEntity {

    @Column(name = "floor_code", nullable = false, length = 50)
    private String floorCode;

    @Column(length = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private ParkingBuilding building;

    @OneToMany(mappedBy = "floor", fetch = FetchType.LAZY)
    private List<ParkingZone> zones = new ArrayList<>();
}
