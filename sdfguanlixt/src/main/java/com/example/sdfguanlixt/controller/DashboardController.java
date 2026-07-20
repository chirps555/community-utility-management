package com.example.sdfguanlixt.controller;

import com.example.sdfguanlixt.common.ApiResult;
import com.example.sdfguanlixt.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ReportService reportService;

    public DashboardController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/admin")
    public ApiResult<Map<String, Object>> admin() {
        return ApiResult.ok(reportService.adminDashboard());
    }

    @GetMapping("/resident")
    public ApiResult<Map<String, Object>> resident(@RequestParam String residentId) {
        return ApiResult.ok(reportService.residentDashboard(residentId));
    }
}
