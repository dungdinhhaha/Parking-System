package com.parking.system.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.parking.system.adapter.ai.PlateRecognitionProvider;
import com.parking.system.dto.request.CheckInRequest;
import com.parking.system.dto.response.ParkingSessionResponse;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.Reservation;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.enums.SessionStatus;
import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.UserRole;
import com.parking.system.enums.VehicleType;
import com.parking.system.exception.BusinessException;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.PlateRecognitionLogRepository;
import com.parking.system.repository.UserRepository;
import com.parking.system.repository.VehicleRepository;
import com.parking.system.storage.FileStorageService;
import com.parking.system.strategy.allocation.AllocationRequest;
import com.parking.system.strategy.allocation.AllocationResult;
import com.parking.system.strategy.allocation.AllocationSource;
import com.parking.system.strategy.allocation.AllocationStrategyFactory;
import com.parking.system.strategy.allocation.ParkingAllocationStrategy;
import com.parking.system.support.TestFixtures;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParkingOperationFacadeTest {

    @Mock private UserRepository userRepository;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private ParkingSessionRepository parkingSessionRepository;
    @Mock private PlateRecognitionLogRepository plateRecognitionLogRepository;
    @Mock private FileStorageService fileStorageService;
    @Mock private PlateRecognitionProvider plateRecognitionProvider;
    @Mock private AllocationStrategyFactory allocationStrategyFactory;

    private ParkingOperationFacade facade;

    @BeforeEach
    void setUp() {
        facade = new ParkingOperationFacade(
                userRepository,
                vehicleRepository,
                parkingSessionRepository,
                plateRecognitionLogRepository,
                fileStorageService,
                plateRecognitionProvider,
                allocationStrategyFactory);
    }

    @Test
    void checkIn_regularCar_createsActiveSession() {
        User staff = TestFixtures.user("staff", UserRole.STAFF);
        Vehicle vehicle = TestFixtures.vehicle("51A-123.45", VehicleType.CAR, staff);
        ParkingZone zone = TestFixtures.carZone(200L, "B1-CAR", "BLD-001");
        ParkingSlot slot = TestFixtures.slot(300L, "C1", zone, SlotStatus.AVAILABLE);

        ParkingAllocationStrategy strategy = org.mockito.Mockito.mock(ParkingAllocationStrategy.class);
        CheckInRequest request = checkInRequest("BLD-001", VehicleType.CAR, "51A-123.45");

        when(userRepository.findByUsername("staff")).thenReturn(Optional.of(staff));
        when(parkingSessionRepository.existsByPlateNumberIgnoreCaseAndStatus("51A-123.45", SessionStatus.ACTIVE)).thenReturn(false);
        when(vehicleRepository.findByPlateNumberIgnoreCase("51A-123.45")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(allocationStrategyFactory.getStrategy(VehicleType.CAR)).thenReturn(strategy);
        when(strategy.allocate(any(AllocationRequest.class))).thenReturn(AllocationResult.builder()
                .assignedZone(zone)
                .assignedSlot(slot)
                .source(AllocationSource.GREEDY)
                .build());
        when(parkingSessionRepository.save(any(ParkingSession.class))).thenAnswer(invocation -> {
            ParkingSession session = invocation.getArgument(0);
            session.setId(80L);
            return session;
        });

        ParkingSessionResponse response = facade.checkIn("staff", request, null);

        assertEquals(80L, response.getId());
        assertEquals(SessionStatus.ACTIVE, response.getStatus());
        assertEquals(AllocationSource.GREEDY, response.getAllocationSource());
        assertEquals(zone.getId(), response.getZoneId());
        assertEquals(slot.getId(), response.getSlotId());
        assertEquals(vehicle.getPlateNumber(), response.getPlateNumber());
        assertNull(response.getReservationId());
        verify(parkingSessionRepository).save(any(ParkingSession.class));
    }

    @Test
    void checkIn_withReservation_usesReservationAllocation() {
        User staff = TestFixtures.user("staff", UserRole.STAFF);
        Vehicle vehicle = TestFixtures.vehicle("51A-123.45", VehicleType.CAR, staff);
        ParkingZone zone = TestFixtures.carZone(200L, "B1-CAR", "BLD-001");
        ParkingSlot slot = TestFixtures.slot(300L, "C1", zone, SlotStatus.RESERVED);
        Reservation reservation = TestFixtures.confirmedCarReservation(staff, vehicle, zone, slot);

        ParkingAllocationStrategy strategy = org.mockito.Mockito.mock(ParkingAllocationStrategy.class);
        CheckInRequest request = checkInRequest("BLD-001", VehicleType.CAR, "51A-123.45");

        when(userRepository.findByUsername("staff")).thenReturn(Optional.of(staff));
        when(parkingSessionRepository.existsByPlateNumberIgnoreCaseAndStatus("51A-123.45", SessionStatus.ACTIVE)).thenReturn(false);
        when(vehicleRepository.findByPlateNumberIgnoreCase("51A-123.45")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(allocationStrategyFactory.getStrategy(VehicleType.CAR)).thenReturn(strategy);
        when(strategy.allocate(any(AllocationRequest.class))).thenReturn(AllocationResult.builder()
                .assignedZone(zone)
                .assignedSlot(slot)
                .matchedReservation(reservation)
                .source(AllocationSource.RESERVATION)
                .build());
        when(parkingSessionRepository.save(any(ParkingSession.class))).thenAnswer(invocation -> {
            ParkingSession session = invocation.getArgument(0);
            session.setId(81L);
            return session;
        });

        ParkingSessionResponse response = facade.checkIn("staff", request, null);

        assertEquals(81L, response.getId());
        assertEquals(AllocationSource.RESERVATION, response.getAllocationSource());
        assertEquals(reservation.getId(), response.getReservationId());
        assertEquals(reservation.getReservationCode(), response.getReservationCode());
        assertNotNull(response.getReservationId());
    }

    @Test
    void checkIn_noSlotAvailable_throwsBusinessException() {
        User staff = TestFixtures.user("staff", UserRole.STAFF);
        CheckInRequest request = checkInRequest("BLD-001", VehicleType.CAR, "51A-123.45");
        ParkingAllocationStrategy strategy = org.mockito.Mockito.mock(ParkingAllocationStrategy.class);

        when(userRepository.findByUsername("staff")).thenReturn(Optional.of(staff));
        when(parkingSessionRepository.existsByPlateNumberIgnoreCaseAndStatus("51A-123.45", SessionStatus.ACTIVE)).thenReturn(false);
        when(vehicleRepository.findByPlateNumberIgnoreCase("51A-123.45")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(allocationStrategyFactory.getStrategy(VehicleType.CAR)).thenReturn(strategy);
        when(strategy.allocate(any(AllocationRequest.class))).thenThrow(new BusinessException("No available car slot"));

        BusinessException ex = assertThrows(BusinessException.class, () -> facade.checkIn("staff", request, null));
        assertEquals("No available car slot", ex.getMessage());
    }

    private CheckInRequest checkInRequest(String buildingCode, VehicleType vehicleType, String plateNumber) {
        CheckInRequest request = new CheckInRequest();
        request.setBuildingCode(buildingCode);
        request.setVehicleType(vehicleType);
        request.setPlateNumber(plateNumber);
        request.setEntryGate("Gate A");
        return request;
    }
}
