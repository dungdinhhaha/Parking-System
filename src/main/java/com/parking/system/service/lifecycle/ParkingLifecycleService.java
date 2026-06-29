package com.parking.system.service.lifecycle;

import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingSubscription;
import com.parking.system.entity.ParkingSubscriptionHistory;
import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.Reservation;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.SessionStatus;
import com.parking.system.enums.SubscriptionStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingSubscriptionHistoryRepository;
import com.parking.system.repository.ParkingSubscriptionRepository;
import com.parking.system.repository.ParkingZoneRepository;
import com.parking.system.repository.ReservationRepository;
import com.parking.system.service.reservation.ReservationResourceManager;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingLifecycleService {

    private static final long RESERVATION_NO_SHOW_GRACE_MINUTES = 15L;

    private final ReservationRepository reservationRepository;
    private final ParkingSubscriptionRepository subscriptionRepository;
    private final ParkingSubscriptionHistoryRepository historyRepository;
    private final ParkingSessionRepository sessionRepository;
    private final ParkingSlotRepository slotRepository;
    private final ParkingZoneRepository zoneRepository;
    private final ReservationResourceManager reservationResourceManager;

    @Scheduled(fixedDelayString = "${parking.lifecycle.fixed-delay-ms:60000}")
    @Transactional
    public void expireDueResources() {
        LocalDateTime now = LocalDateTime.now();
        expireReservations(now);
        expireSubscriptions(now);
    }

    private void expireReservations(LocalDateTime now) {
        for (Reservation candidate : reservationRepository
                .findAllByStatusAndEndTimeBefore(ReservationStatus.CONFIRMED, now)) {
            expireReservationIfEligible(candidate.getId(), now);
        }

        LocalDateTime noShowDeadline = now.minusMinutes(RESERVATION_NO_SHOW_GRACE_MINUTES);
        for (Reservation candidate : reservationRepository
                .findAllByStatusAndStartTimeBefore(ReservationStatus.CONFIRMED, noShowDeadline)) {
            expireReservationIfEligible(candidate.getId(), now);
        }
    }

    private void expireReservationIfEligible(Long reservationId, LocalDateTime now) {
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId).orElse(null);
        if (reservation == null || reservation.getStatus() != ReservationStatus.CONFIRMED) {
            return;
        }

        boolean endExpired = reservation.getEndTime() != null && reservation.getEndTime().isBefore(now);
        boolean noShowExpired = reservation.getStartTime() != null
                && reservation.getStartTime().plusMinutes(RESERVATION_NO_SHOW_GRACE_MINUTES).isBefore(now);

        if (!endExpired && !noShowExpired) {
            return;
        }

        reservationResourceManager.release(reservation);
        reservation.expire();
        reservationRepository.save(reservation);
    }

    private void expireSubscriptions(LocalDateTime now) {
        for (ParkingSubscription candidate : subscriptionRepository
                .findAllByStatusAndEndAtBefore(SubscriptionStatus.ACTIVE, now)) {
            ParkingSubscription subscription = subscriptionRepository.findByIdForUpdate(candidate.getId()).orElse(null);
            if (subscription == null
                    || subscription.getStatus() != SubscriptionStatus.ACTIVE
                    || subscription.getEndAt() == null
                    || !subscription.getEndAt().isBefore(now)) {
                continue;
            }

            boolean inUse = sessionRepository.existsBySubscription_IdAndStatus(
                    subscription.getId(), SessionStatus.ACTIVE);
            if (!inUse) {
                releaseDedicatedResource(subscription);
            }
            subscription.expire();
            subscriptionRepository.save(subscription);
            recordExpiration(subscription, inUse);
        }
    }

    private void releaseDedicatedResource(ParkingSubscription subscription) {
        if (subscription.getVehicleType() == VehicleType.CAR) {
            if (subscription.getAssignedSlot() == null) {
                return;
            }
            ParkingSlot slot = slotRepository.findByIdForUpdate(subscription.getAssignedSlot().getId()).orElse(null);
            if (slot != null) {
                slot.release();
                slotRepository.save(slot);
            }
            return;
        }

        if (subscription.getAssignedZone() == null) {
            return;
        }
        ParkingZone zone = zoneRepository.findByIdForUpdate(subscription.getAssignedZone().getId()).orElse(null);
        if (zone != null) {
            zone.decreaseReservedCount();
            zoneRepository.save(zone);
        }
    }

    private void recordExpiration(ParkingSubscription subscription, boolean inUse) {
        ParkingSubscriptionHistory history = new ParkingSubscriptionHistory();
        history.setSubscription(subscription);
        history.setAction("EXPIRE");
        history.setOldRfidCardId(subscription.getRfidCardId());
        history.setNewRfidCardId(subscription.getRfidCardId());
        history.setOldVehicleId(subscription.getVehicle() == null ? null : subscription.getVehicle().getId());
        history.setNewVehicleId(history.getOldVehicleId());
        history.setOldZoneId(subscription.getAssignedZone() == null ? null : subscription.getAssignedZone().getId());
        history.setNewZoneId(history.getOldZoneId());
        history.setOldSlotId(subscription.getAssignedSlot() == null ? null : subscription.getAssignedSlot().getId());
        history.setNewSlotId(history.getOldSlotId());
        history.setNotes(inUse
                ? "Expired while vehicle was parked; resource will be released at check-out"
                : "Expired automatically and dedicated resource released");
        historyRepository.save(history);
    }
}
