package com.parking.system.entity;

import com.parking.system.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vehicle extends BaseEntity {
    private String plateNumber;
    private VehicleType vehicleType;
    private User owner;
}
