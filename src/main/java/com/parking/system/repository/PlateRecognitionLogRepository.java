package com.parking.system.repository;

import com.parking.system.entity.PlateRecognitionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlateRecognitionLogRepository extends JpaRepository<PlateRecognitionLog, Long> {
}
