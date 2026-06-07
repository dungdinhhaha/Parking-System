package com.parking.system.entity;

import com.parking.system.enums.VehicleType;
import com.parking.system.enums.ZoneStatus;
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
        name = "parking_zones",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_parking_zones_floor_zone_code", columnNames = {"floor_id", "zone_code"})
        }
)
@Getter
@Setter
public class ParkingZone extends BaseEntity {

    @Column(name = "zone_code", nullable = false, length = 50)
    private String zoneCode;

    @Column(length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private Integer capacity = 0;

    @Column(nullable = false)
    private Integer currentCount = 0;

    @Column(nullable = false)
    private Integer reservedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ZoneStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private ParkingFloor floor;

    @OneToMany(mappedBy = "zone", fetch = FetchType.LAZY)
    private List<ParkingSlot> slots = new ArrayList<>();

    @OneToMany(mappedBy = "assignedZone", fetch = FetchType.LAZY)
    private List<ParkingSession> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "assignedZone", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    public void increaseCurrentCount() {
        currentCount = safe(currentCount) + 1;
        refreshStatus();
    }

    public void decreaseCurrentCount() {
        currentCount = Math.max(0, safe(currentCount) - 1);
        refreshStatus();
    }

    public void increaseReservedCount() {
        reservedCount = safe(reservedCount) + 1;
        refreshStatus();
    }

    public void decreaseReservedCount() {
        reservedCount = Math.max(0, safe(reservedCount) - 1);
        refreshStatus();
    }

    public int getAvailableCapacity() {
        return Math.max(0, safe(capacity) - safe(currentCount) - safe(reservedCount));
    }

    public boolean hasAvailableCapacity() {
        return getAvailableCapacity() > 0;
    }

    public boolean isFull() {
        return safe(currentCount) >= safe(capacity);
    }

    private int safe(Integer value) {
        return value == null ? 0 : value;
    }

    private void refreshStatus() {
        if (status == ZoneStatus.MAINTENANCE || status == ZoneStatus.LOCKED) {
            return;
        }
        status = isFull() ? ZoneStatus.FULL : ZoneStatus.AVAILABLE;
    }
}
