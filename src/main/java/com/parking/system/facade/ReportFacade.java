package com.parking.system.facade;

import com.parking.system.dto.response.DashboardReportResponse;
import com.parking.system.dto.response.IncidentReportResponse;
import com.parking.system.dto.response.OccupancyReportResponse;
import com.parking.system.dto.response.RevenueReportResponse;
import com.parking.system.service.report.DashboardReportCalculator;
import com.parking.system.service.report.IncidentReportCalculator;
import com.parking.system.service.report.OccupancyReportCalculator;
import com.parking.system.service.report.RevenueReportCalculator;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportFacade {

    private final DashboardReportCalculator dashboardReportCalculator;
    private final RevenueReportCalculator revenueReportCalculator;
    private final OccupancyReportCalculator occupancyReportCalculator;
    private final IncidentReportCalculator incidentReportCalculator;

    @Transactional(readOnly = true)
    public DashboardReportResponse getDashboard(LocalDateTime from, LocalDateTime to) {
        return dashboardReportCalculator.calculate(from, to);
    }

    @Transactional(readOnly = true)
    public RevenueReportResponse getRevenue(LocalDateTime from, LocalDateTime to) {
        return revenueReportCalculator.calculate(from, to);
    }

    @Transactional(readOnly = true)
    public OccupancyReportResponse getOccupancy() {
        return occupancyReportCalculator.calculate();
    }

    @Transactional(readOnly = true)
    public IncidentReportResponse getIncidents(LocalDateTime from, LocalDateTime to) {
        return incidentReportCalculator.calculate(from, to);
    }
}
