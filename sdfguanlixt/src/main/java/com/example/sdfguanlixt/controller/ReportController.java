package com.example.sdfguanlixt.controller;

import com.example.sdfguanlixt.common.ApiResult;
import com.example.sdfguanlixt.common.PageResult;
import com.example.sdfguanlixt.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/by-time")
    public ApiResult<PageResult<Map<String, Object>>> byTime(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        List<Map<String, Object>> list = reportService.byTime(start, end);
        return ApiResult.ok(new PageResult<>(list, list.size()));
    }

    @GetMapping("/by-building")
    public ApiResult<PageResult<Map<String, Object>>> byBuilding() {
        List<Map<String, Object>> list = reportService.byBuilding();
        return ApiResult.ok(new PageResult<>(list, list.size()));
    }

    @GetMapping("/by-type")
    public ApiResult<PageResult<Map<String, Object>>> byType() {
        List<Map<String, Object>> list = reportService.byType();
        return ApiResult.ok(new PageResult<>(list, list.size()));
    }

    @GetMapping("/payment")
    public ApiResult<PageResult<Map<String, Object>>> payment(
            @RequestParam(required = false) String period) {
        List<Map<String, Object>> list = reportService.payment(period);
        return ApiResult.ok(new PageResult<>(list, list.size()));
    }
}
