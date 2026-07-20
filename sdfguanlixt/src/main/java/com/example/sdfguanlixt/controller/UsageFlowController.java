package com.example.sdfguanlixt.controller;

import com.example.sdfguanlixt.common.ApiResult;
import com.example.sdfguanlixt.common.PageResult;
import com.example.sdfguanlixt.entity.UsageFlow;
import com.example.sdfguanlixt.repository.UsageFlowRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usage-flows")
public class UsageFlowController {

    private final UsageFlowRepository usageFlowRepository;

    public UsageFlowController(UsageFlowRepository usageFlowRepository) {
        this.usageFlowRepository = usageFlowRepository;
    }

    @GetMapping
    public ApiResult<PageResult<UsageFlow>> list(
            @RequestParam(required = false) String residentId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String period) {
        String rid = StringUtils.hasText(residentId) ? residentId : null;
        String t = StringUtils.hasText(type) ? type : null;
        String p = StringUtils.hasText(period) ? period : null;
        var list = usageFlowRepository.search(rid, t, p);
        return ApiResult.ok(new PageResult<>(list, list.size()));
    }
}
