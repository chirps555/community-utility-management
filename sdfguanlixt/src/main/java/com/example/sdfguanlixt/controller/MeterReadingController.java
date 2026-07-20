package com.example.sdfguanlixt.controller;

import com.example.sdfguanlixt.common.ApiResult;
import com.example.sdfguanlixt.common.PageResult;
import com.example.sdfguanlixt.dto.MeterReadingRequest;
import com.example.sdfguanlixt.dto.MeterReadingResult;
import com.example.sdfguanlixt.entity.MeterReading;
import com.example.sdfguanlixt.service.MeterReadingService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meter-readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    @GetMapping
    public ApiResult<PageResult<MeterReading>> list(
            @RequestParam(required = false) String residentId,
            @RequestParam(required = false) String period) {
        var list = meterReadingService.list(residentId, period);
        return ApiResult.ok(new PageResult<>(list, list.size()));
    }

    @PostMapping
    public ApiResult<MeterReadingResult> create(@Valid @RequestBody MeterReadingRequest request) {
        return ApiResult.ok(meterReadingService.create(request));
    }
}
