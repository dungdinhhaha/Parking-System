package com.parking.system.repository;

import com.parking.system.entity.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByParkingSession_Id(Long parkingSessionId);
    List<Payment> findAllByOrderByCreatedAtDesc();
}
