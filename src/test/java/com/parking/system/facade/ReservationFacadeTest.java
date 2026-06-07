package com.parking.system.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parking.system.dto.request.CreateReservationRequest;
import com.parking.system.dto.response.ReservationResponse;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.Reservation;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.UserRole;
import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import com.parking.system.repository.ReservationRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.service.reservation.ReservationAvailabilityQueryService;
import com.parking.system.service.reservation.ReservationMapper;
import com.parking.system.service.reservation.ReservationResourceManager;
import com.parking.system.service.reservation.ReservationValidator;
import com.parking.system.service.reservation.ReservationVehicleResolver;
import com.parking.system.strategy.reservation.ReservationAllocationResult;
import com.parking.system.strategy.reservation.ReservationAllocationStrategy;
import com.parking.system.strategy.reservation.ReservationAllocationStrategyFactory;
import com.parking.system.support.TestFixtures;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationFacadeTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReservationValidator validator;
    @Mock private ReservationVehicleResolver vehicleResolver;
    @Mock private ReservationAllocationStrategyFactory allocationStrategyFactory;
    @Mock private ParkingSlotRepository parkingSlotRepository;
    @Mock private ParkingZoneRepository parkingZoneRepository;
    @Mock private ReservationAvailabilityQueryService availabilityQueryService;

    private ReservationFacade facade;
    private ReservationMapper mapper;
    private ReservationResourceManager resourceManager;

    @BeforeEach
    void setUp() {
        mapper = new ReservationMapper();
        resourceManager = new ReservationResourceManager(parkingSlotRepository, parkingZoneRepository);
        facade = new ReservationFacade(
                reservationRepository,
                userRepository,
                validator,
                vehicleResolver,
                allocationStrategyFactory,
                resourceManager,
                availabilityQueryService,
                mapper);
    }

    @Test
    void create_carReservation_success() {
        User user = TestFixtures.user("driver", UserRole.DRIVER);
        Vehicle vehicle = TestFixtures.vehicle("51A-123.45", VehicleType.CAR, user);
        ParkingZone zone = TestFixtures.carZone(200L, "B1-CAR", "BLD-001");
        ParkingSlot slot = TestFixtures.slot(300L, "C1", zone, com.parking.system.enums.SlotStatus.AVAILABLE);

        CreateReservationRequest request = reservationRequest(VehicleType.CAR, zone.getId(), slot.getId());

        ReservationAllocationStrategy strategy = org.mockito.Mockito.mock(ReservationAllocationStrategy.class);
        when(userRepository.findByUsername("driver")).thenReturn(java.util.Optional.of(user));
        when(vehicleResolver.resolve(user, request)).thenReturn(vehicle);
        when(allocationStrategyFactory.getStrategy(VehicleType.CAR)).thenReturn(strategy);
        when(strategy.allocate(request)).thenReturn(ReservationAllocationResult.builder()
                .assignedZone(zone)
                .assignedSlot(slot)
                .build());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.setId(55L);
            return reservation;
        });

        ReservationResponse response = facade.create("driver", request);

        assertEquals(55L, response.getId());
        assertEquals(ReservationStatus.CONFIRMED, response.getStatus());
        assertEquals(vehicle.getPlateNumber(), response.getPlateNumber());
        assertEquals(zone.getId(), response.getZoneId());
        assertEquals(slot.getId(), response.getSlotId());
        assertNotNull(response.getReservationCode());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void cancel_reservation_releasesSlotAndMarksCancelled() {
        User user = TestFixtures.user("driver", UserRole.DRIVER);
        Vehicle vehicle = TestFixtures.vehicle("51A-123.45", VehicleType.CAR, user);
        ParkingZone zone = TestFixtures.carZone(200L, "B1-CAR", "BLD-001");
        ParkingSlot slot = TestFixtures.slot(300L, "C1", zone, com.parking.system.enums.SlotStatus.RESERVED);
        Reservation reservation = TestFixtures.confirmedCarReservation(user, vehicle, zone, slot);

        when(reservationRepository.findById(50L)).thenReturn(java.util.Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(parkingSlotRepository.save(any(ParkingSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = facade.cancel("driver", 50L);

        assertEquals(ReservationStatus.CANCELLED, response.getStatus());
        assertEquals(com.parking.system.enums.SlotStatus.AVAILABLE, slot.getStatus());
        verify(parkingSlotRepository).save(slot);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void get_notOwnedReservation_throwsAccessDenied() {
        User owner = TestFixtures.user("owner", UserRole.DRIVER);
        User other = TestFixtures.user("other", UserRole.DRIVER);
        Vehicle vehicle = TestFixtures.vehicle("51A-123.45", VehicleType.CAR, owner);
        ParkingZone zone = TestFixtures.carZone(200L, "B1-CAR", "BLD-001");
        ParkingSlot slot = TestFixtures.slot(300L, "C1", zone, com.parking.system.enums.SlotStatus.RESERVED);
        Reservation reservation = TestFixtures.confirmedCarReservation(owner, vehicle, zone, slot);

        when(reservationRepository.findById(50L)).thenReturn(java.util.Optional.of(reservation));

        BusinessException ex = assertThrows(BusinessException.class, () -> facade.get("other", 50L));
        assertEquals("Access denied", ex.getMessage());
    }

    private CreateReservationRequest reservationRequest(VehicleType type, Long zoneId, Long slotId) {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setPlateNumber("51A-123.45");
        request.setVehicleType(type);
        request.setStartTime(LocalDateTime.now().minusMinutes(1));
        request.setEndTime(LocalDateTime.now().plusHours(2));
        request.setZoneId(zoneId);
        request.setSlotId(slotId);
        return request;
    }
}
