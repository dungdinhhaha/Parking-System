package com.parking.system.config;

import com.parking.system.entity.ParkingBuilding;
import com.parking.system.entity.ParkingFloor;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.enums.BuildingStatus;
import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.enums.ZoneStatus;
import com.parking.system.repository.ParkingBuildingRepository;
import com.parking.system.repository.ParkingFloorRepository;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import java.time.LocalTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Configuration
public class InfrastructureSeedConfig {

    @Bean
    @Profile("!test")
    @Order(3)
    public CommandLineRunner seedInfrastructure(ParkingBuildingRepository buildingRepository,
                                                ParkingFloorRepository floorRepository,
                                                ParkingZoneRepository zoneRepository,
                                                ParkingSlotRepository slotRepository) {
        return args -> {
            ParkingBuilding building = buildingRepository.findByBuildingCodeIgnoreCase("BLD-001")
                    .orElseGet(() -> {
                        ParkingBuilding created = new ParkingBuilding();
                        created.setBuildingCode("BLD-001");
                        created.setName("Central Parking");
                        created.setAddress("123 Main Street, District 1");
                        created.setOpenTime(LocalTime.of(5, 0));
                        created.setCloseTime(LocalTime.of(23, 0));
                        created.setStatus(BuildingStatus.OPEN);
                        created.setDescription("Default seed building for parking operations");
                        return buildingRepository.save(created);
                    });

            ParkingFloor b1 = floorRepository.findByBuilding_IdAndFloorCodeIgnoreCase(building.getId(), "B1")
                    .orElseGet(() -> {
                        ParkingFloor floor = new ParkingFloor();
                        floor.setBuilding(building);
                        floor.setFloorCode("B1");
                        floor.setName("Basement 1");
                        floor.setDescription("Primary access floor");
                        return floorRepository.save(floor);
                    });

            ParkingFloor b2 = floorRepository.findByBuilding_IdAndFloorCodeIgnoreCase(building.getId(), "B2")
                    .orElseGet(() -> {
                        ParkingFloor floor = new ParkingFloor();
                        floor.setBuilding(building);
                        floor.setFloorCode("B2");
                        floor.setName("Basement 2");
                        floor.setDescription("Secondary parking floor");
                        return floorRepository.save(floor);
                    });

            ParkingZone b1Car = zoneRepository.findByFloor_IdAndZoneCodeIgnoreCase(b1.getId(), "B1-CAR")
                    .orElseGet(() -> {
                        ParkingZone zone = new ParkingZone();
                        zone.setFloor(b1);
                        zone.setZoneCode("B1-CAR");
                        zone.setName("Car Zone B1");
                        zone.setVehicleType(VehicleType.CAR);
                        zone.setCapacity(20);
                        zone.setCurrentCount(0);
                        zone.setReservedCount(0);
                        zone.setStatus(ZoneStatus.AVAILABLE);
                        return zoneRepository.save(zone);
                    });

            ParkingZone b1Motor = zoneRepository.findByFloor_IdAndZoneCodeIgnoreCase(b1.getId(), "B1-MOTOR")
                    .orElseGet(() -> {
                        ParkingZone zone = new ParkingZone();
                        zone.setFloor(b1);
                        zone.setZoneCode("B1-MOTOR");
                        zone.setName("Motorbike Zone B1");
                        zone.setVehicleType(VehicleType.MOTORBIKE);
                        zone.setCapacity(40);
                        zone.setCurrentCount(0);
                        zone.setReservedCount(0);
                        zone.setStatus(ZoneStatus.AVAILABLE);
                        return zoneRepository.save(zone);
                    });

            ParkingZone b2Car = zoneRepository.findByFloor_IdAndZoneCodeIgnoreCase(b2.getId(), "B2-CAR")
                    .orElseGet(() -> {
                        ParkingZone zone = new ParkingZone();
                        zone.setFloor(b2);
                        zone.setZoneCode("B2-CAR");
                        zone.setName("Car Zone B2");
                        zone.setVehicleType(VehicleType.CAR);
                        zone.setCapacity(15);
                        zone.setCurrentCount(0);
                        zone.setReservedCount(0);
                        zone.setStatus(ZoneStatus.AVAILABLE);
                        return zoneRepository.save(zone);
                    });

            ParkingZone b2Motor = zoneRepository.findByFloor_IdAndZoneCodeIgnoreCase(b2.getId(), "B2-MOTOR")
                    .orElseGet(() -> {
                        ParkingZone zone = new ParkingZone();
                        zone.setFloor(b2);
                        zone.setZoneCode("B2-MOTOR");
                        zone.setName("Motorbike Zone B2");
                        zone.setVehicleType(VehicleType.MOTORBIKE);
                        zone.setCapacity(25);
                        zone.setCurrentCount(0);
                        zone.setReservedCount(0);
                        zone.setStatus(ZoneStatus.AVAILABLE);
                        return zoneRepository.save(zone);
                    });

            createSlotIfMissing(slotRepository, b1Car, "C1", VehicleType.CAR, 12.5, SlotStatus.AVAILABLE);
            createSlotIfMissing(slotRepository, b1Car, "C2", VehicleType.CAR, 15.0, SlotStatus.AVAILABLE);
            createSlotIfMissing(slotRepository, b1Car, "C3", VehicleType.CAR, 18.5, SlotStatus.AVAILABLE);
            createSlotIfMissing(slotRepository, b2Car, "C4", VehicleType.CAR, 10.0, SlotStatus.AVAILABLE);
            createSlotIfMissing(slotRepository, b2Car, "C5", VehicleType.CAR, 14.0, SlotStatus.AVAILABLE);
            createSlotIfMissing(slotRepository, b1Motor, "M1", VehicleType.MOTORBIKE, 3.0, SlotStatus.AVAILABLE);
            createSlotIfMissing(slotRepository, b1Motor, "M2", VehicleType.MOTORBIKE, 4.0, SlotStatus.AVAILABLE);
            createSlotIfMissing(slotRepository, b2Motor, "M3", VehicleType.MOTORBIKE, 2.5, SlotStatus.AVAILABLE);
            createSlotIfMissing(slotRepository, b2Motor, "M4", VehicleType.MOTORBIKE, 5.0, SlotStatus.AVAILABLE);
        };
    }

    private void createSlotIfMissing(ParkingSlotRepository slotRepository,
                                     ParkingZone zone,
                                     String slotCode,
                                     VehicleType vehicleType,
                                     Double distanceFromGate,
                                     SlotStatus status) {
        if (slotRepository.findByZone_IdAndSlotCodeIgnoreCase(zone.getId(), slotCode).isPresent()) {
            return;
        }

        ParkingSlot slot = new ParkingSlot();
        slot.setZone(zone);
        slot.setSlotCode(slotCode);
        slot.setVehicleType(vehicleType);
        slot.setDistanceFromGate(distanceFromGate);
        slot.setStatus(status);
        slotRepository.save(slot);
    }
}
