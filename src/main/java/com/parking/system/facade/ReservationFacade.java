package com.parking.system.facade;

import com.parking.system.dto.request.CreateReservationRequest;
import com.parking.system.dto.response.ReservationAvailabilityResponse;
import com.parking.system.dto.response.ReservationResponse;
import com.parking.system.entity.Reservation;
import com.parking.system.entity.User;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.UserRole;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ReservationRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.service.reservation.ReservationAvailabilityQueryService;
import com.parking.system.service.reservation.ReservationMapper;
import com.parking.system.service.reservation.ReservationResourceManager;
import com.parking.system.service.reservation.ReservationValidator;
import com.parking.system.service.reservation.ReservationVehicleResolver;
import com.parking.system.strategy.reservation.ReservationAllocationResult;
import com.parking.system.strategy.reservation.ReservationAllocationStrategyFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ReservationValidator validator;
    private final ReservationVehicleResolver vehicleResolver;
    private final ReservationAllocationStrategyFactory allocationStrategyFactory;
    private final ReservationResourceManager resourceManager;
    private final ReservationAvailabilityQueryService availabilityQueryService;
    private final ReservationMapper mapper;

    @Transactional
    public ReservationResponse create(String username, CreateReservationRequest request) {
        validator.validateCreateRequest(request);
        User user = getUser(username);
        var vehicle = vehicleResolver.resolve(user, request);

        ReservationAllocationResult allocationResult = allocationStrategyFactory
                .getStrategy(request.getVehicleType())
                .allocate(request);

        Reservation reservation = new Reservation();
        reservation.setReservationCode("RSV-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservation.setPlateNumber(request.getPlateNumber().toUpperCase());
        reservation.setVehicleType(request.getVehicleType());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setUser(user);
        reservation.setVehicle(vehicle);
        reservation.setAssignedZone(allocationResult.getAssignedZone());
        reservation.setAssignedSlot(allocationResult.getAssignedSlot());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.confirm();
        return mapper.toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse cancel(String username, Long reservationId) {
        Reservation reservation = getOwnedReservation(username, reservationId);
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return mapper.toResponse(reservation);
        }

        resourceManager.release(reservation);
        reservation.cancel();
        return mapper.toResponse(reservationRepository.save(reservation));
    }

    @Transactional(readOnly = true)
    public ReservationResponse get(String username, Long reservationId) {
        return mapper.toResponse(getOwnedReservation(username, reservationId));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(String username) {
        return reservationRepository.findAllByUser_UsernameOrderByCreatedAtDesc(username).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAll() {
        return reservationRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationAvailabilityResponse getAvailability(String buildingCode) {
        return availabilityQueryService.getAvailability(buildingCode);
    }

    private Reservation getOwnedReservation(String username, Long reservationId) {
        User currentUser = getUser(username);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException("Reservation not found"));
        if (currentUser.getRole() != UserRole.MANAGER
                && currentUser.getRole() != UserRole.SYSTEM_ADMIN
                && !reservation.getUser().getUsername().equalsIgnoreCase(username)) {
            throw new BusinessException("Access denied");
        }
        return reservation;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));
    }
}
