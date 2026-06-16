package com.parking.system.stub;

import com.parking.system.entity.PlateRecognitionLog;
import com.parking.system.repository.PlateRecognitionLogRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPlateRecognitionLogRepository implements PlateRecognitionLogRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, PlateRecognitionLog> logs = new LinkedHashMap<>();

    @Override
    public PlateRecognitionLog save(PlateRecognitionLog log) {
        if (log.getId() == null) {
            log.setId(sequence.getAndIncrement());
        }
        logs.put(log.getId(), log);
        return log;
    }
}
