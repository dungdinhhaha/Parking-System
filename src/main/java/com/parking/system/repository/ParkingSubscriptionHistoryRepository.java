package com.parking.system.repository;

import com.parking.system.entity.ParkingSubscriptionHistory;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSubscriptionHistoryRepository extends JpaRepository<ParkingSubscriptionHistory, Long> {

    @EntityGraph(attributePaths = {"changedBy"})
    List<ParkingSubscriptionHistory> findAllBySubscription_IdOrderByCreatedAtDesc(Long subscriptionId);
}
