package com.example.sdfguanlixt.controller;

import com.example.sdfguanlixt.common.ApiResult;
import com.example.sdfguanlixt.common.PageResult;
import com.example.sdfguanlixt.dto.BillCalculationResult;
import com.example.sdfguanlixt.dto.PayRequest;
import com.example.sdfguanlixt.dto.PayResult;
import com.example.sdfguanlixt.entity.PaymentOrder;
import com.example.sdfguanlixt.service.OrderService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ApiResult<PageResult<PaymentOrder>> list(
            @RequestParam(required = false) String residentId,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String status) {
        var list = orderService.list(residentId, period, status);
        return ApiResult.ok(new PageResult<>(list, list.size()));
    }

    @GetMapping("/calculate")
    public ApiResult<BillCalculationResult> calculate(
            @RequestParam String residentId,
            @RequestParam String period) {
        return ApiResult.ok(orderService.calculateBill(residentId, period));
    }

    @PostMapping("/generate-bill")
    public ApiResult<PaymentOrder> generateBill(
            @RequestParam String residentId,
            @RequestParam String period) {
        return ApiResult.ok(orderService.generateBill(residentId, period));
    }

    @PostMapping("/simulate-full-payment")
    public ApiResult<PayResult> simulateFullPayment(
            @RequestParam String residentId,
            @RequestParam String period,
            @Valid @RequestBody PayRequest request) {
        return ApiResult.ok(orderService.simulateFullPayment(residentId, period, request));
    }

    @PostMapping("/{id}/pay")
    public ApiResult<PayResult> pay(@PathVariable String id, @Valid @RequestBody PayRequest request) {
        return ApiResult.ok(orderService.simulatePay(id, request));
    }

    @GetMapping("/{id}")
    public ApiResult<PaymentOrder> get(@PathVariable String id) {
        return ApiResult.ok(orderService.getById(id));
    }

    @PostMapping
    public ApiResult<PaymentOrder> create(@RequestBody PaymentOrder order) {
        return ApiResult.ok(orderService.create(order));
    }

    @PutMapping("/{id}")
    public ApiResult<PaymentOrder> update(@PathVariable String id, @RequestBody PaymentOrder order) {
        return ApiResult.ok(orderService.update(id, order));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable String id) {
        orderService.delete(id);
        return ApiResult.ok();
    }
}
