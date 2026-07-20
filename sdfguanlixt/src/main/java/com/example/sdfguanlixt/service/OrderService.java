package com.example.sdfguanlixt.service;

import com.example.sdfguanlixt.common.BizConstants;
import com.example.sdfguanlixt.common.BusinessException;
import com.example.sdfguanlixt.common.IdUtil;
import com.example.sdfguanlixt.common.TimeUtil;
import com.example.sdfguanlixt.dto.BillCalculationResult;
import com.example.sdfguanlixt.dto.PayRequest;
import com.example.sdfguanlixt.dto.PayResult;
import com.example.sdfguanlixt.entity.PaymentOrder;
import com.example.sdfguanlixt.entity.Resident;
import com.example.sdfguanlixt.repository.PaymentOrderRepository;
import com.example.sdfguanlixt.repository.ResidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class OrderService {

    private final PaymentOrderRepository orderRepository;
    private final ResidentRepository residentRepository;
    private final FeeCalculationService feeCalculationService;
    private final PaymentSimulationService paymentSimulationService;

    public OrderService(
            PaymentOrderRepository orderRepository,
            ResidentRepository residentRepository,
            FeeCalculationService feeCalculationService,
            PaymentSimulationService paymentSimulationService) {
        this.orderRepository = orderRepository;
        this.residentRepository = residentRepository;
        this.feeCalculationService = feeCalculationService;
        this.paymentSimulationService = paymentSimulationService;
    }

    public List<PaymentOrder> list(String residentId, String period, String status) {
        String rid = StringUtils.hasText(residentId) ? residentId : null;
        String p = StringUtils.hasText(period) ? period : null;
        String s = StringUtils.hasText(status) ? status : null;
        return orderRepository.search(rid, p, s);
    }

    public PaymentOrder getById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, BizConstants.MSG_ORDER_NOT_FOUND));
    }

    public BillCalculationResult calculateBill(String residentId, String period) {
        return feeCalculationService.calculate(residentId, period);
    }

    @Transactional
    public PaymentOrder generateBill(String residentId, String period) {
        BillCalculationResult bill = feeCalculationService.calculate(residentId, period);

        var existing = orderRepository.findByResidentIdAndPeriod(residentId, period.trim());
        if (existing.isPresent()) {
            PaymentOrder order = existing.get();
            if (BizConstants.STATUS_PAID.equals(order.getStatus())) {
                throw new BusinessException(400, BizConstants.MSG_ORDER_PERIOD_PAID);
            }
            order.setWaterFee(bill.getWaterFee());
            order.setElectricFee(bill.getElectricFee());
            order.setTotal(bill.getTotal());
            return orderRepository.save(order);
        }

        PaymentOrder order = new PaymentOrder();
        order.setId(IdUtil.genId("O"));
        order.setResidentId(residentId);
        order.setResidentName(bill.getResidentName());
        order.setPeriod(period.trim());
        order.setWaterFee(bill.getWaterFee());
        order.setElectricFee(bill.getElectricFee());
        order.setTotal(bill.getTotal());
        order.setStatus(BizConstants.STATUS_PENDING);
        order.setCreateTime(TimeUtil.nowDateTime());
        return orderRepository.save(order);
    }

    @Transactional
    public PayResult simulatePay(String id, PayRequest request) {
        return paymentSimulationService.simulatePay(id, request);
    }

    @Transactional
    public PayResult simulateFullPayment(String residentId, String period, PayRequest request) {
        PaymentOrder order = generateBill(residentId, period);
        return paymentSimulationService.simulatePay(order.getId(), request);
    }

    @Transactional
    public PaymentOrder pay(String id) {
        PayRequest request = new PayRequest();
        request.setPayMethod("wechat");
        request.setPayPassword("123456");
        return paymentSimulationService.simulatePay(id, request).getOrder();
    }

    public PaymentOrder create(PaymentOrder order) {
        if (!StringUtils.hasText(order.getId())) {
            order.setId(IdUtil.genId("O"));
        }
        if (!StringUtils.hasText(order.getCreateTime())) {
            order.setCreateTime(TimeUtil.nowDateTime());
        }
        fillResidentName(order);
        order.setTotal(calcTotal(order));
        if (!StringUtils.hasText(order.getStatus())) {
            order.setStatus(BizConstants.STATUS_PENDING);
        }
        return orderRepository.save(order);
    }

    public PaymentOrder update(String id, PaymentOrder data) {
        PaymentOrder existing = getById(id);
        if (StringUtils.hasText(data.getPeriod())) {
            existing.setPeriod(data.getPeriod());
        }
        if (data.getWaterFee() != null) {
            existing.setWaterFee(data.getWaterFee());
        }
        if (data.getElectricFee() != null) {
            existing.setElectricFee(data.getElectricFee());
        }
        if (StringUtils.hasText(data.getStatus())) {
            existing.setStatus(data.getStatus());
            if (BizConstants.STATUS_PAID.equals(data.getStatus()) && !StringUtils.hasText(data.getPayTime())) {
                existing.setPayTime(TimeUtil.nowDateTime());
            }
        }
        if (data.getPayTime() != null) {
            existing.setPayTime(data.getPayTime().isEmpty() ? null : data.getPayTime());
        }
        existing.setTotal(calcTotal(existing));
        return orderRepository.save(existing);
    }

    public void delete(String id) {
        if (!orderRepository.existsById(id)) {
            throw new BusinessException(404, BizConstants.MSG_ORDER_NOT_FOUND);
        }
        orderRepository.deleteById(id);
    }

    private void fillResidentName(PaymentOrder order) {
        if (StringUtils.hasText(order.getResidentName())) {
            return;
        }
        residentRepository.findById(order.getResidentId())
                .map(Resident::getName)
                .ifPresent(order::setResidentName);
    }

    private double calcTotal(PaymentOrder order) {
        double water = order.getWaterFee() != null ? order.getWaterFee() : 0;
        double electric = order.getElectricFee() != null ? order.getElectricFee() : 0;
        return Math.round((water + electric) * 100.0) / 100.0;
    }
}
