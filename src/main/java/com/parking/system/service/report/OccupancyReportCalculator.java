package com.parking.system.service.report;

import com.parking.system.dto.response.BuildingOccupancyResponse;
import com.parking.system.dto.response.OccupancyReportResponse;
import com.parking.system.entity.ParkingBuilding;
import com.parking.system.entity.ParkingFloor;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.repository.ParkingBuildingRepository;
import com.parking.system.repository.ParkingFloorRepository;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OccupancyReportCalculator {

    private final ParkingBuildingRepository buildingRepository;
    private final ParkingFloorRepository floorRepository;
    private final ParkingZoneRepository zoneRepository;
    private final ParkingSlotRepository slotRepository;

    public OccupancyReportCalculator(ParkingBuildingRepository buildingRepository,
                                     ParkingFloorRepository floorRepository,
                                     ParkingZoneRepository zoneRepository,
                                     ParkingSlotRepository slotRepository) {
        this.buildingRepository = buildingRepository;
        this.floorRepository = floorRepository;
        this.zoneRepository = zoneRepository;
        this.slotRepository = slotRepository;
    }

    public OccupancyReportResponse calculate() {
        List<ParkingBuilding> buildings = buildingRepository.findAll();
        List<ParkingFloor> floors = floorRepository.findAll();
        List<ParkingZone> zones = zoneRepository.findAll();
        List<ParkingSlot> slots = slotRepository.findAll();

        Map<Long, List<ParkingFloor>> floorsByBuilding = floors.stream()
                .collect(Collectors.groupingBy(floor -> floor.getBuilding().getId()));
        Map<Long, List<ParkingZone>> zonesByFloor = zones.stream()
                .collect(Collectors.groupingBy(zone -> zone.getFloor().getId()));
        Map<Long, List<ParkingSlot>> slotsByZone = slots.stream()
                .collect(Collectors.groupingBy(slot -> slot.getZone().getId()));

        List<BuildingOccupancyResponse> buildingOccupancy = buildings.stream()
                .sorted(Comparator.comparing(ParkingBuilding::getBuildingCode, String.CASE_INSENSITIVE_ORDER))
                .map(building -> buildBuildingOccupancy(building, floorsByBuilding, zonesByFloor, slotsByZone))
                .toList();

        long totalBuildings = buildings.size();
        long totalFloors = floors.size();
        long totalZones = zones.size();
        long totalSlots = slots.size();

        long availableCarSlots = buildingOccupancy.stream().mapToLong(BuildingOccupancyResponse::getAvailableCarSlots).sum();
        long occupiedCarSlots = buildingOccupancy.stream().mapToLong(BuildingOccupancyResponse::getOccupiedCarSlots).sum();
        long reservedCarSlots = buildingOccupancy.stream().mapToLong(BuildingOccupancyResponse::getReservedCarSlots).sum();
        long motorbikeCapacity = buildingOccupancy.stream().mapToLong(BuildingOccupancyResponse::getMotorbikeCapacity).sum();
        long motorbikeCurrentCount = buildingOccupancy.stream().mapToLong(BuildingOccupancyResponse::getMotorbikeCurrentCount).sum();
        long motorbikeReservedCount = buildingOccupancy.stream().mapToLong(BuildingOccupancyResponse::getMotorbikeReservedCount).sum();
        long totalCapacity = buildingOccupancy.stream().mapToLong(BuildingOccupancyResponse::getTotalCapacity).sum();
        long usedCapacity = buildingOccupancy.stream().mapToLong(BuildingOccupancyResponse::getUsedCapacity).sum();
        long availableCapacity = Math.max(0, totalCapacity - usedCapacity);

        return OccupancyReportResponse.builder()
                .totalBuildings(totalBuildings)
                .totalFloors(totalFloors)
                .totalZones(totalZones)
                .totalSlots(totalSlots)
                .availableCarSlots(availableCarSlots)
                .occupiedCarSlots(occupiedCarSlots)
                .reservedCarSlots(reservedCarSlots)
                .motorbikeCapacity(motorbikeCapacity)
                .motorbikeCurrentCount(motorbikeCurrentCount)
                .motorbikeReservedCount(motorbikeReservedCount)
                .totalCapacity(totalCapacity)
                .usedCapacity(usedCapacity)
                .availableCapacity(availableCapacity)
                .occupancyRate(rate(usedCapacity, totalCapacity))
                .buildingOccupancy(buildingOccupancy)
                .build();
    }

    private BuildingOccupancyResponse buildBuildingOccupancy(ParkingBuilding building,
                                                             Map<Long, List<ParkingFloor>> floorsByBuilding,
                                                             Map<Long, List<ParkingZone>> zonesByFloor,
                                                             Map<Long, List<ParkingSlot>> slotsByZone) {
        List<ParkingFloor> buildingFloors = floorsByBuilding.getOrDefault(building.getId(), List.of());
        List<ParkingZone> buildingZones = buildingFloors.stream()
                .flatMap(floor -> zonesByFloor.getOrDefault(floor.getId(), List.of()).stream())
                .toList();
        List<ParkingSlot> buildingSlots = buildingZones.stream()
                .flatMap(zone -> slotsByZone.getOrDefault(zone.getId(), List.of()).stream())
                .toList();

        long totalFloors = buildingFloors.size();
        long totalZones = buildingZones.size();
        long totalSlots = buildingSlots.size();

        long availableCarSlots = buildingSlots.stream()
                .filter(slot -> slot.getVehicleType() == VehicleType.CAR)
                .filter(slot -> slot.getStatus() == SlotStatus.AVAILABLE)
                .count();
        long occupiedCarSlots = buildingSlots.stream()
                .filter(slot -> slot.getVehicleType() == VehicleType.CAR)
                .filter(slot -> slot.getStatus() == SlotStatus.OCCUPIED)
                .count();
        long reservedCarSlots = buildingSlots.stream()
                .filter(slot -> slot.getVehicleType() == VehicleType.CAR)
                .filter(slot -> slot.getStatus() == SlotStatus.RESERVED)
                .count();

        long motorbikeCapacity = buildingZones.stream()
                .filter(zone -> zone.getVehicleType() == VehicleType.MOTORBIKE)
                .mapToLong(zone -> zone.getCapacity() == null ? 0L : zone.getCapacity())
                .sum();
        long motorbikeCurrentCount = buildingZones.stream()
                .filter(zone -> zone.getVehicleType() == VehicleType.MOTORBIKE)
                .mapToLong(zone -> zone.getCurrentCount() == null ? 0L : zone.getCurrentCount())
                .sum();
        long motorbikeReservedCount = buildingZones.stream()
                .filter(zone -> zone.getVehicleType() == VehicleType.MOTORBIKE)
                .mapToLong(zone -> zone.getReservedCount() == null ? 0L : zone.getReservedCount())
                .sum();

        long totalCapacity = totalSlots + motorbikeCapacity;
        long usedCapacity = occupiedCarSlots + reservedCarSlots + motorbikeCurrentCount + motorbikeReservedCount;
        long availableCapacity = Math.max(0, totalCapacity - usedCapacity);

        return BuildingOccupancyResponse.builder()
                .buildingId(building.getId())
                .buildingCode(building.getBuildingCode())
                .buildingName(building.getName())
                .totalFloors(totalFloors)
                .totalZones(totalZones)
                .totalSlots(totalSlots)
                .availableCarSlots(availableCarSlots)
                .occupiedCarSlots(occupiedCarSlots)
                .reservedCarSlots(reservedCarSlots)
                .motorbikeCapacity(motorbikeCapacity)
                .motorbikeCurrentCount(motorbikeCurrentCount)
                .motorbikeReservedCount(motorbikeReservedCount)
                .totalCapacity(totalCapacity)
                .usedCapacity(usedCapacity)
                .availableCapacity(availableCapacity)
                .occupancyRate(rate(usedCapacity, totalCapacity))
                .build();
    }

    private BigDecimal rate(long used, long total) {
        if (total <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(used)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }
}
