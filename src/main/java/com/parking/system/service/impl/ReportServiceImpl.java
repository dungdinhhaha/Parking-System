package com.parking.system.service.impl;

import com.parking.system.dto.response.DashboardReportResponse;
import com.parking.system.dto.response.IncidentReportResponse;
import com.parking.system.dto.response.OccupancyReportResponse;
import com.parking.system.dto.response.RevenueReportResponse;
import com.parking.system.facade.ReportFacade;
import com.parking.system.service.ReportService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportFacade reportFacade;

    @Override
    public DashboardReportResponse getDashboard(LocalDateTime from, LocalDateTime to) {
        return reportFacade.getDashboard(from, to);
    }

    @Override
    public RevenueReportResponse getRevenue(LocalDateTime from, LocalDateTime to) {
        return reportFacade.getRevenue(from, to);
    }

    @Override
    public OccupancyReportResponse getOccupancy() {
        return reportFacade.getOccupancy();
    }

    @Override
    public IncidentReportResponse getIncidents(LocalDateTime from, LocalDateTime to) {
        return reportFacade.getIncidents(from, to);
    }
}
