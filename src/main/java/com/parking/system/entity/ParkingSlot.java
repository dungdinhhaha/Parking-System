package com.parking.system.entity;

import com.parking.system.enums.SlotStatus;
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
        name = "parking_slots",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_parking_slots_zone_slot_code", columnNames = {"zone_id", "slot_code"})
        }
)
@Getter
@Setter
public class ParkingSlot extends BaseEntity {

    @Column(name = "slot_code", nullable = false, length = 50)
    private String slotCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SlotStatus status;

    private Double distanceFromGate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private ParkingZone zone;

    @OneToMany(mappedBy = "assignedSlot", fetch = FetchType.LAZY)
    private List<ParkingSession> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "assignedSlot", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    public void occupy() {
        status = SlotStatus.OCCUPIED;
    }

    public void occupyReserved() {
        status = SlotStatus.OCCUPIED;
    }

    public void release() {
        status = SlotStatus.AVAILABLE;
    }

    public void reserve() {
        status = SlotStatus.RESERVED;
    }

    public void lock() {
        status = SlotStatus.LOCKED;
    }

    public void markMaintenance() {
        status = SlotStatus.MAINTENANCE;
    }

    public boolean isAvailable() {
        return status == SlotStatus.AVAILABLE;
    }

    public boolean isReserved() {
        return status == SlotStatus.RESERVED;
    }
}
