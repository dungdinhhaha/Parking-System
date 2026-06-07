package com.parking.system.service.reservation;

import com.parking.system.dto.response.ParkingSlotResponse;
import com.parking.system.dto.response.ParkingZoneResponse;
import com.parking.system.dto.response.ReservationAvailabilityResponse;
import com.parking.system.dto.response.ReservationResponse;
import com.parking.system.entity.ParkingBuilding;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.Reservation;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .reservationCode(reservation.getReservationCode())
                .plateNumber(reservation.getPlateNumber())
                .vehicleType(reservation.getVehicleType())
                .status(reservation.getStatus())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .usedAt(reservation.getUsedAt())
                .userId(reservation.getUser() != null ? reservation.getUser().getId() : null)
                .username(reservation.getUser() != null ? reservation.getUser().getUsername() : null)
                .zoneId(reservation.getAssignedZone() != null ? reservation.getAssignedZone().getId() : null)
                .zoneCode(reservation.getAssignedZone() != null ? reservation.getAssignedZone().getZoneCode() : null)
                .slotId(reservation.getAssignedSlot() != null ? reservation.getAssignedSlot().getId() : null)
                .slotCode(reservation.getAssignedSlot() != null ? reservation.getAssignedSlot().getSlotCode() : null)
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }

    public ParkingSlotResponse toSlotResponse(ParkingSlot slot) {
        return ParkingSlotResponse.builder()
                .id(slot.getId())
                .zoneId(slot.getZone().getId())
                .zoneCode(slot.getZone().getZoneCode())
                .slotCode(slot.getSlotCode())
                .vehicleType(slot.getVehicleType())
                .status(slot.getStatus())
                .distanceFromGate(slot.getDistanceFromGate())
                .createdAt(slot.getCreatedAt())
                .updatedAt(slot.getUpdatedAt())
                .build();
    }

    public ParkingZoneResponse toZoneResponse(ParkingZone zone) {
        return ParkingZoneResponse.builder()
                .id(zone.getId())
                .floorId(zone.getFloor().getId())
                .floorCode(zone.getFloor().getFloorCode())
                .zoneCode(zone.getZoneCode())
                .name(zone.getName())
                .vehicleType(zone.getVehicleType())
                .capacity(zone.getCapacity())
                .currentCount(zone.getCurrentCount())
                .reservedCount(zone.getReservedCount())
                .availableCapacity(zone.getAvailableCapacity())
                .status(zone.getStatus())
                .slotCount((long) zone.getSlots().size())
                .createdAt(zone.getCreatedAt())
                .updatedAt(zone.getUpdatedAt())
                .build();
    }

    public ReservationAvailabilityResponse toAvailabilityResponse(
            ParkingBuilding building,
            List<ParkingSlotResponse> availableCarSlots,
            List<ParkingZoneResponse> availableMotorbikeZones) {
        return ReservationAvailabilityResponse.builder()
                .buildingCode(building.getBuildingCode())
                .buildingName(building.getName())
                .availableCarSlots(availableCarSlots)
                .availableMotorbikeZones(availableMotorbikeZones)
                .carSlotCount((long) availableCarSlots.size())
                .motorbikeZoneCount((long) availableMotorbikeZones.size())
                .selectedVehicleType(null)
                .build();
    }
}
