package com.parking.system.service.report;

import com.parking.system.dto.response.DashboardReportResponse;
import com.parking.system.entity.ParkingBuilding;
import com.parking.system.entity.ParkingFloor;
import com.parking.system.entity.ParkingSession;
import com.parking.system.entity.ParkingSlot;
import com.parking.system.entity.ParkingZone;
import com.parking.system.entity.Reservation;
import com.parking.system.enums.IncidentStatus;
import com.parking.system.enums.ReservationStatus;
import com.parking.system.enums.SessionStatus;
import com.parking.system.repository.ParkingBuildingRepository;
import com.parking.system.repository.ParkingFloorRepository;
import com.parking.system.repository.ParkingSessionRepository;
import com.parking.system.repository.ParkingSlotRepository;
import com.parking.system.repository.ParkingZoneRepository;
import com.parking.system.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DashboardReportCalculator {

    private final ParkingBuildingRepository buildingRepository;
    private final ParkingFloorRepository floorRepository;
    private final ParkingZoneRepository zoneRepository;
    private final ParkingSlotRepository slotRepository;
    private final ReservationRepository reservationRepository;
    private final ParkingSessionRepository sessionRepository;
    private final RevenueReportCalculator revenueReportCalculator;
    private final OccupancyReportCalculator occupancyReportCalculator;
    private final IncidentReportCalculator incidentReportCalculator;

    public DashboardReportCalculator(ParkingBuildingRepository buildingRepository,
                                     ParkingFloorRepository floorRepository,
                                     ParkingZoneRepository zoneRepository,
                                     ParkingSlotRepository slotRepository,
                                     ReservationRepository reservationRepository,
                                     ParkingSessionRepository sessionRepository,
                                     RevenueReportCalculator revenueReportCalculator,
                                     OccupancyReportCalculator occupancyReportCalculator,
                                     IncidentReportCalculator incidentReportCalculator) {
        this.buildingRepository = buildingRepository;
        this.floorRepository = floorRepository;
        this.zoneRepository = zoneRepository;
        this.slotRepository = slotRepository;
        this.reservationRepository = reservationRepository;
        this.sessionRepository = sessionRepository;
        this.revenueReportCalculator = revenueReportCalculator;
        this.occupancyReportCalculator = occupancyReportCalculator;
        this.incidentReportCalculator = incidentReportCalculator;
    }

    public DashboardReportResponse calculate(LocalDateTime from, LocalDateTime to) {
        List<Reservation> reservations = reservationRepository.findAll().stream()
                .filter(reservation -> isWithin(reservation.getCreatedAt(), from, to))
                .toList();
        List<ParkingSession> sessions = sessionRepository.findAll().stream()
                .filter(session -> isWithin(session.getCreatedAt(), from, to) || isWithin(session.getCheckInTime(), from, to) || isWithin(session.getCheckOutTime(), from, to))
                .toList();

        return DashboardReportResponse.builder()
                .from(from)
                .to(to)
                .totalBuildings(buildingRepository.count())
                .totalFloors(floorRepository.count())
                .totalZones(zoneRepository.count())
                .totalSlots(slotRepository.count())
                .totalReservations(reservations.size())
                .confirmedReservations(countReservationStatus(reservations, ReservationStatus.CONFIRMED))
                .usedReservations(countReservationStatus(reservations, ReservationStatus.USED))
                .cancelledReservations(countReservationStatus(reservations, ReservationStatus.CANCELLED))
                .activeSessions(countSessionStatus(sessions, SessionStatus.ACTIVE))
                .completedSessions(countSessionStatus(sessions, SessionStatus.COMPLETED))
                .unpaidSessions(countSessionStatus(sessions, SessionStatus.UNPAID))
                .revenue(revenueReportCalculator.calculate(from, to))
                .occupancy(occupancyReportCalculator.calculate())
                .incidents(incidentReportCalculator.calculate(from, to))
                .build();
    }

    private long countReservationStatus(List<Reservation> reservations, ReservationStatus status) {
        return reservations.stream().filter(reservation -> reservation.getStatus() == status).count();
    }

    private long countSessionStatus(List<ParkingSession> sessions, SessionStatus status) {
        return sessions.stream().filter(session -> session.getStatus() == status).count();
    }

    private boolean isWithin(LocalDateTime value, LocalDateTime from, LocalDateTime to) {
        if (value == null) {
            return false;
        }
        return (from == null || !value.isBefore(from)) && (to == null || !value.isAfter(to));
    }
}
