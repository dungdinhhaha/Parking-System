package com.parking.system.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingFloor extends BaseEntity {
    private String floorCode;
    private ParkingBuilding building;
}
