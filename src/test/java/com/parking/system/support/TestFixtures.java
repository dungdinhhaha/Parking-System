package com.parking.system.support;

import com.parking.system.entity.ParkingFloor;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.Payment;
import com.parking.system.entity.Reservation;
import com.parking.system.entity.User;
import com.parking.system.entity.Vehicle;
import com.parking.system.enums.BuildingStatus;
import com.parking.system.enums.IncidentStatus;
import com.parking.system.enums.IncidentType;
import com.parking.system.enums.PaymentMethod;
import com.parking.system.enums.PaymentStatus;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.SessionStatus;
import com.parking.system.enums.SlotStatus;
import com.parking.system.enums.UserRole;
import com.parking.system.enums.UserStatus;
import com.parking.system.enums.VehicleType;
import com.parking.system.enums.ZoneStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TestFixtures {

    private TestFixtures() {
    }

    public static User user(String username, UserRole role) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setFullName(username + " full");
        user.setPassword("password");
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    public static Vehicle vehicle(String plateNumber, VehicleType type, User owner) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(10L);
        vehicle.setPlateNumber(plateNumber);
        vehicle.setVehicleType(type);
        vehicle.setOwner(owner);
        return vehicle;
    }

    public static ParkingZone carZone(Long id, String code, String buildingCode) {
        ParkingZone zone = new ParkingZone();
        zone.setId(id);
        zone.setZoneCode(code);
        zone.setVehicleType(VehicleType.CAR);
        zone.setCapacity(10);
        zone.setCurrentCount(0);
        zone.setReservedCount(0);
        zone.setStatus(ZoneStatus.AVAILABLE);
        ParkingFloor floor = floor(100L, "B1", buildingCode);
        zone.setFloor(floor);
        return zone;
    }

    public static ParkingZone motorbikeZone(Long id, String code, String buildingCode, int capacity, int current, int reserved) {
        ParkingZone zone = new ParkingZone();
        zone.setId(id);
        zone.setZoneCode(code);
        zone.setVehicleType(VehicleType.MOTORBIKE);
        zone.setCapacity(capacity);
        zone.setCurrentCount(current);
        zone.setReservedCount(reserved);
        zone.setStatus(ZoneStatus.AVAILABLE);
        ParkingFloor floor = floor(100L, "B1", buildingCode);
        zone.setFloor(floor);
        return zone;
    }

    public static ParkingFloor floor(Long id, String code, String buildingCode) {
        ParkingFloor floor = new ParkingFloor();
        floor.setId(id);
        floor.setFloorCode(code);
        var building = new com.parking.system.entity.ParkingBuilding();
        building.setId(999L);
        building.setBuildingCode(buildingCode);
        building.setName("Test Building");
        building.setStatus(BuildingStatus.OPEN);
        floor.setBuilding(building);
        return floor;
    }

    public static ParkingSlot slot(Long id, String code, ParkingZone zone, SlotStatus status) {
        ParkingSlot slot = new ParkingSlot();
        slot.setId(id);
        slot.setSlotCode(code);
        slot.setVehicleType(VehicleType.CAR);
        slot.setZone(zone);
        slot.setStatus(status);
        slot.setDistanceFromGate(10.0);
        return slot;
    }

    public static Reservation confirmedCarReservation(User user, Vehicle vehicle, ParkingZone zone, ParkingSlot slot) {
        Reservation reservation = new Reservation();
        reservation.setId(50L);
        reservation.setReservationCode("RSV-TEST01");
        reservation.setPlateNumber(vehicle.getPlateNumber());
        reservation.setVehicleType(VehicleType.CAR);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setStartTime(LocalDateTime.now().minusHours(1));
        reservation.setEndTime(LocalDateTime.now().plusHours(2));
        reservation.setUser(user);
        reservation.setVehicle(vehicle);
        reservation.setAssignedZone(zone);
        reservation.setAssignedSlot(slot);
        return reservation;
    }

    public static ParkingSession activeSession(User owner, Vehicle vehicle, ParkingZone zone, ParkingSlot slot) {
        ParkingSession session = new ParkingSession();
        session.setId(70L);
        session.setTicketCode("TCK-TEST01");
        session.setPlateNumber(vehicle.getPlateNumber());
        session.setVehicleType(vehicle.getVehicleType());
        session.setStatus(SessionStatus.ACTIVE);
        session.setCheckInTime(LocalDateTime.now().minusHours(1));
        session.setVehicle(vehicle);
        session.setAssignedZone(zone);
        session.setAssignedSlot(slot);
        return session;
    }

    public static Payment paidCashPayment(ParkingSession session, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setId(90L);
        payment.setParkingSession(session);
        payment.setAmount(amount);
        payment.setMethod(PaymentMethod.CASH);
        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionCode("CASH-PAID-TEST");
        payment.setPaidAt(LocalDateTime.now());
        return payment;
    }

    public static Payment failedCashPayment(ParkingSession session, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setId(91L);
        payment.setParkingSession(session);
        payment.setAmount(amount);
        payment.setMethod(PaymentMethod.CASH);
        payment.setStatus(PaymentStatus.FAILED);
        return payment;
    }
}
