package com.parking.system.controller.report;

import com.parking.system.dto.response.ApiResponse;
import com.parking.system.dto.response.DashboardReportResponse;
import com.parking.system.dto.response.IncidentReportResponse;
import com.parking.system.dto.response.OccupancyReportResponse;
import com.parking.system.dto.response.RevenueReportResponse;
import com.parking.system.service.ReportService;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ADMIN')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardReportResponse> dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        LocalDateTime[] range = normalizeRange(from, to);
        return ApiResponse.success(reportService.getDashboard(range[0], range[1]));
    }

    @GetMapping("/revenue")
    public ApiResponse<RevenueReportResponse> revenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        LocalDateTime[] range = normalizeRange(from, to);
        return ApiResponse.success(reportService.getRevenue(range[0], range[1]));
    }

    @GetMapping("/occupancy")
    public ApiResponse<OccupancyReportResponse> occupancy() {
        return ApiResponse.success(reportService.getOccupancy());
    }

    @GetMapping("/incidents")
    public ApiResponse<IncidentReportResponse> incidents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        LocalDateTime[] range = normalizeRange(from, to);
        return ApiResponse.success(reportService.getIncidents(range[0], range[1]));
    }

    private LocalDateTime[] normalizeRange(LocalDateTime from, LocalDateTime to) {
        LocalDateTime resolvedEnd = Objects.requireNonNullElseGet(to, LocalDateTime::now);
        LocalDateTime start = Objects.requireNonNullElseGet(from, () -> resolvedEnd.minusDays(30));
        LocalDateTime end = resolvedEnd;
        if (start.isAfter(end)) {
            LocalDateTime tmp = start;
            start = end;
            end = tmp;
        }
        return new LocalDateTime[] {start, end};
    }
}
