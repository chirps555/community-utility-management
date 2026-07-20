package com.example.sdfguanlixt.controller;

import com.example.sdfguanlixt.common.ApiResult;
import com.example.sdfguanlixt.common.PageResult;
import com.example.sdfguanlixt.entity.Utility;
import com.example.sdfguanlixt.service.UtilityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilities")
public class UtilityController {

    private final UtilityService utilityService;

    public UtilityController(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    @GetMapping
    public ApiResult<PageResult<Utility>> list(@RequestParam(required = false) String residentId) {
        var list = utilityService.list(residentId);
        return ApiResult.ok(new PageResult<>(list, list.size()));
    }

    @GetMapping("/{id}")
    public ApiResult<Utility> get(@PathVariable String id) {
        return ApiResult.ok(utilityService.getById(id));
    }

    @PostMapping
    public ApiResult<Utility> create(@RequestBody Utility utility) {
        return ApiResult.ok(utilityService.create(utility));
    }

    @PutMapping("/{id}")
    public ApiResult<Utility> update(@PathVariable String id, @RequestBody Utility utility) {
        return ApiResult.ok(utilityService.update(id, utility));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable String id) {
        utilityService.delete(id);
        return ApiResult.ok();
    }
}
