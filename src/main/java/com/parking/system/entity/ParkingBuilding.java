package com.parking.system.entity;

import com.parking.system.enums.ZoneStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingBuilding extends BaseEntity {
    private String buildingCode;
    private String name;
    private ZoneStatus status;
}
