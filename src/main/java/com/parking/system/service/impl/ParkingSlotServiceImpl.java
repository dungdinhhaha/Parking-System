package com.parking.system.service.impl;

import com.parking.system.dto.request.CreateParkingSlotRequest;
import com.parking.system.dto.request.UpdateParkingSlotRequest;
import com.parking.system.dto.response.ParkingSlotResponse;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.enums.SlotStatus;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import com.parking.system.repository.ReservationRepository;
import com.parking.system.service.ParkingSlotService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingSlotServiceImpl implements ParkingSlotService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingZoneRepository parkingZoneRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public ParkingSlotResponse create(CreateParkingSlotRequest request) {
        if (parkingSlotRepository.findByZone_IdAndSlotCodeIgnoreCase(request.getZoneId(), request.getSlotCode()).isPresent()) {
            throw new BusinessException("Parking slot already exists in this zone");
        }
        ParkingSlot slot = new ParkingSlot();
        slot.setZone(parkingZoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new BusinessException("Parking zone not found")));
        apply(slot, request.getSlotCode(), request.getVehicleType(), request.getDistanceFromGate(), request.getStatus());
        return toResponse(parkingSlotRepository.save(slot));
    }

    @Override
    @Transactional
    public ParkingSlotResponse update(Long id, UpdateParkingSlotRequest request) {
        ParkingSlot slot = getEntity(id);
        parkingSlotRepository.findByZone_IdAndSlotCodeIgnoreCase(slot.getZone().getId(), request.getSlotCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Parking slot already exists in this zone");
                });
        apply(slot, request.getSlotCode(), request.getVehicleType(), request.getDistanceFromGate(), request.getStatus());
        return toResponse(parkingSlotRepository.save(slot));
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingSlotResponse get(Long id) {
        return toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingSlotResponse> getAll() {
        return parkingSlotRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ParkingSlot slot = getEntity(id);
        if (parkingSessionRepository.existsByAssignedSlot_Id(slot.getId()) || reservationRepository.existsByAssignedSlot_Id(slot.getId())) {
            throw new BusinessException("Cannot delete slot while sessions or reservations still exist");
        }
        parkingSlotRepository.delete(slot);
    }

    private ParkingSlot getEntity(Long id) {
        return parkingSlotRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Parking slot not found"));
    }

    private void apply(ParkingSlot slot,
                       String slotCode,
                       com.parking.system.enums.VehicleType vehicleType,
                       Double distanceFromGate,
                       SlotStatus status) {
        slot.setSlotCode(slotCode);
        slot.setVehicleType(vehicleType);
        slot.setDistanceFromGate(distanceFromGate);
        slot.setStatus(status != null ? status : SlotStatus.AVAILABLE);
    }

    private ParkingSlotResponse toResponse(ParkingSlot slot) {
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
}
