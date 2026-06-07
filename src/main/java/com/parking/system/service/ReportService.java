package com.parking.system.service;

import com.parking.system.dto.response.DashboardReportResponse;
import com.parking.system.dto.response.IncidentReportResponse;
import com.parking.system.dto.response.OccupancyReportResponse;
import com.parking.system.dto.response.RevenueReportResponse;
import java.time.LocalDateTime;

public interface ReportService {
    DashboardReportResponse getDashboard(LocalDateTime from, LocalDateTime to);
    RevenueReportResponse getRevenue(LocalDateTime from, LocalDateTime to);
    OccupancyReportResponse getOccupancy();
    IncidentReportResponse getIncidents(LocalDateTime from, LocalDateTime to);
}
