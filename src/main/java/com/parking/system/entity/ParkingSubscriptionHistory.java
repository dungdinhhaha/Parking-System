package com.parking.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "parking_subscription_history")
@Getter
@Setter
public class ParkingSubscriptionHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private ParkingSubscription subscription;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "old_rfid_card_id", length = 100)
    private String oldRfidCardId;

    @Column(name = "new_rfid_card_id", length = 100)
    private String newRfidCardId;

    @Column(name = "old_vehicle_id")
    private Long oldVehicleId;

    @Column(name = "new_vehicle_id")
    private Long newVehicleId;

    @Column(name = "old_zone_id")
    private Long oldZoneId;

    @Column(name = "new_zone_id")
    private Long newZoneId;

    @Column(name = "old_slot_id")
    private Long oldSlotId;

    @Column(name = "new_slot_id")
    private Long newSlotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id")
    private User changedBy;

    @Column(length = 500)
    private String notes;
}
