package com.parking.system.service.reservation;

import com.parking.system.dto.request.CreateReservationRequest;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.repository.VehicleRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationVehicleResolver {

    private final VehicleRepository vehicleRepository;

    public Vehicle resolve(User user, CreateReservationRequest request) {
        return vehicleRepository.findByOwner_IdAndPlateNumberIgnoreCase(user.getId(), request.getPlateNumber())
                .orElseGet(() -> {
                    Vehicle created = new Vehicle();
                    created.setOwner(user);
                    created.setPlateNumber(request.getPlateNumber().toUpperCase(Locale.ROOT));
                    created.setVehicleType(request.getVehicleType());
                    return vehicleRepository.save(created);
                });
    }
}
