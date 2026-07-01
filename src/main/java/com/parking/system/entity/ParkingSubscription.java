package com.parking.system.entity;

import com.parking.system.enums.SubscriptionCycleType;
import com.parking.system.enums.SubscriptionStatus;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "parking_subscriptions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_parking_subscriptions_subscription_code", columnNames = "subscription_code")
        }
)
@Getter
@Setter
public class ParkingSubscription extends BaseEntity {

    @Column(name = "subscription_code", nullable = false, length = 50)
    private String subscriptionCode;

    @Column(name = "rfid_card_id", nullable = false, length = 100)
    private String rfidCardId;

    @Column(name = "subscriber_name", length = 255)
    private String subscriberName;

    @Column(name = "subscriber_phone", length = 50)
    private String subscriberPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false, length = 50)
    private SubscriptionCycleType subscriptionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SubscriptionStatus status;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(length = 255)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_zone_id")
    private ParkingZone assignedZone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_slot_id")
    private ParkingSlot assignedSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    private List<ParkingSession> sessions = new ArrayList<>();

    public void activate() {
        status = SubscriptionStatus.ACTIVE;
    }

    public void cancel() {
        status = SubscriptionStatus.CANCELLED;
    }

    public void expire() {
        status = SubscriptionStatus.EXPIRED;
    }

    public boolean isActiveAt(LocalDateTime time) {
        if (status != SubscriptionStatus.ACTIVE || time == null) {
            return false;
        }
        return !time.isBefore(startAt) && !time.isAfter(endAt);
    }
}
