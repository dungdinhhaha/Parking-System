package com.parking.system.repository;

import com.parking.system.entity.ParkingSubscription;
import com.parking.system.enums.SubscriptionStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParkingSubscriptionRepository extends JpaRepository<ParkingSubscription, Long> {
    Optional<ParkingSubscription> findBySubscriptionCodeIgnoreCase(String subscriptionCode);

    List<ParkingSubscription> findAllByOrderByCreatedAtDesc();
    List<ParkingSubscription> findAllByVehicle_Owner_UsernameIgnoreCaseOrderByCreatedAtDesc(String username);

    boolean existsByVehicle_IdAndStatus(Long vehicleId, SubscriptionStatus status);
    boolean existsByRfidCardIdIgnoreCaseAndStatus(String rfidCardId, SubscriptionStatus status);

    Optional<ParkingSubscription> findFirstByVehicle_IdAndStatusOrderByCreatedAtDesc(Long vehicleId, SubscriptionStatus status);

    Optional<ParkingSubscription> findFirstByVehicle_PlateNumberIgnoreCaseOrderByCreatedAtDesc(String plateNumber);
    List<ParkingSubscription> findAllByVehicle_PlateNumberIgnoreCaseOrderByCreatedAtDesc(String plateNumber);

    Optional<ParkingSubscription> findFirstByRfidCardIdIgnoreCaseAndStatusAndStartAtLessThanEqualAndEndAtGreaterThanEqualOrderByCreatedAtDesc(
            String rfidCardId,
            SubscriptionStatus status,
            LocalDateTime startAt,
            LocalDateTime endAt);

    boolean existsByRfidCardIdIgnoreCaseAndStatusAndIdNot(String rfidCardId, SubscriptionStatus status, Long id);
    boolean existsByVehicle_IdAndStatusAndIdNot(Long vehicleId, SubscriptionStatus status, Long id);
    List<ParkingSubscription> findAllByStatusAndEndAtBefore(SubscriptionStatus status, LocalDateTime endAt);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select subscription from ParkingSubscription subscription
            join fetch subscription.vehicle
            left join fetch subscription.assignedZone
            left join fetch subscription.assignedSlot
            where subscription.id = :id
            """)
    Optional<ParkingSubscription> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select subscription from ParkingSubscription subscription
            join fetch subscription.vehicle
            left join fetch subscription.assignedZone
            left join fetch subscription.assignedSlot
            where upper(subscription.rfidCardId) = upper(:rfidCardId)
              and subscription.status = :status
              and subscription.startAt <= :at
              and subscription.endAt >= :at
            order by subscription.createdAt desc
            """)
    List<ParkingSubscription> findActiveByRfidForUpdate(@Param("rfidCardId") String rfidCardId,
                                                        @Param("status") SubscriptionStatus status,
                                                        @Param("at") LocalDateTime at);
}
