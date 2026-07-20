package com.example.sdfguanlixt.controller;

import com.example.sdfguanlixt.common.ApiResult;
import com.example.sdfguanlixt.common.PageResult;
import com.example.sdfguanlixt.entity.Resident;
import com.example.sdfguanlixt.service.ResidentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/residents")
public class ResidentController {

    private final ResidentService residentService;

    public ResidentController(ResidentService residentService) {
        this.residentService = residentService;
    }

    @GetMapping
    public ApiResult<PageResult<Resident>> list(@RequestParam(required = false) String keyword) {
        var list = residentService.list(keyword);
        return ApiResult.ok(new PageResult<>(list, list.size()));
    }

    @GetMapping("/{id}")
    public ApiResult<Resident> get(@PathVariable String id) {
        return ApiResult.ok(residentService.getById(id));
    }

    @PostMapping
    public ApiResult<Resident> create(@RequestBody Resident resident) {
        return ApiResult.ok(residentService.create(resident));
    }

    @PutMapping("/{id}")
    public ApiResult<Resident> update(@PathVariable String id, @RequestBody Resident resident) {
        return ApiResult.ok(residentService.update(id, resident));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable String id) {
        residentService.delete(id);
        return ApiResult.ok();
    }
}
