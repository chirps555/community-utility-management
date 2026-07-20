package com.example.sdfguanlixt.service;

import com.example.sdfguanlixt.common.BizConstants;
import com.example.sdfguanlixt.entity.PaymentOrder;
import com.example.sdfguanlixt.entity.Resident;
import com.example.sdfguanlixt.repository.MeterReadingRepository;
import com.example.sdfguanlixt.repository.PaymentOrderRepository;
import com.example.sdfguanlixt.repository.ResidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final PaymentOrderRepository orderRepository;
    private final ResidentRepository residentRepository;
    private final MeterReadingRepository meterReadingRepository;

    public ReportService(
            PaymentOrderRepository orderRepository,
            ResidentRepository residentRepository,
            MeterReadingRepository meterReadingRepository) {
        this.orderRepository = orderRepository;
        this.residentRepository = residentRepository;
        this.meterReadingRepository = meterReadingRepository;
    }

    public List<Map<String, Object>> byTime(String start, String end) {
        Map<String, Map<String, Object>> map = new LinkedHashMap<>();
        for (PaymentOrder o : orderRepository.findAll()) {
            if (!inRange(o.getPeriod(), start, end)) {
                continue;
            }
            map.computeIfAbsent(o.getPeriod(), p -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("period", p);
                row.put("orderCount", 0);
                row.put("paidCount", 0);
                row.put("totalAmount", 0.0);
                row.put("paidAmount", 0.0);
                return row;
            });
            Map<String, Object> s = map.get(o.getPeriod());
            s.put("orderCount", (int) s.get("orderCount") + 1);
            s.put("totalAmount", (double) s.get("totalAmount") + safe(o.getTotal()));
            if (BizConstants.STATUS_PAID.equals(o.getStatus())) {
                s.put("paidCount", (int) s.get("paidCount") + 1);
                s.put("paidAmount", (double) s.get("paidAmount") + safe(o.getTotal()));
            }
        }
        return map.values().stream().map(this::formatAmounts)
                .sorted((a, b) -> String.valueOf(a.get("period")).compareTo(String.valueOf(b.get("period"))))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> byBuilding() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Resident r : residentRepository.findAll()) {
            List<PaymentOrder> orders = orderRepository.search(r.getId(), null, null);
            double total = orders.stream().mapToDouble(o -> safe(o.getTotal())).sum();
            double unpaid = orders.stream()
                    .filter(o -> BizConstants.STATUS_PENDING.equals(o.getStatus()))
                    .mapToDouble(o -> safe(o.getTotal()))
                    .sum();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("building", r.getBuilding());
            row.put("unit", r.getUnit());
            row.put("residentName", r.getName());
            row.put("orderCount", orders.size());
            row.put("totalAmount", fmt(total));
            row.put("unpaid", fmt(unpaid));
            result.add(row);
        }
        return result;
    }

    public List<Map<String, Object>> byType() {
        Map<String, List<Resident>> byType = residentRepository.findAll().stream()
                .collect(Collectors.groupingBy(Resident::getType));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Resident>> e : byType.entrySet()) {
            List<String> ids = e.getValue().stream().map(Resident::getId).collect(Collectors.toList());
            List<PaymentOrder> orders = orderRepository.findAll().stream()
                    .filter(o -> ids.contains(o.getResidentId()))
                    .collect(Collectors.toList());
            long paid = orders.stream().filter(o -> BizConstants.STATUS_PAID.equals(o.getStatus())).count();
            double totalAmount = orders.stream().mapToDouble(o -> safe(o.getTotal())).sum();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("type", e.getKey());
            row.put("residentCount", e.getValue().size());
            row.put("orderCount", orders.size());
            row.put("totalAmount", fmt(totalAmount));
            row.put("paidRate", orders.isEmpty() ? "0%"
                    : String.format("%.0f%%", paid * 100.0 / orders.size()));
            result.add(row);
        }
        return result;
    }

    public List<Map<String, Object>> payment(String period) {
        List<PaymentOrder> orders = StringUtils.hasText(period)
                ? orderRepository.search(null, period, null)
                : orderRepository.findAll();
        return orders.stream().map(o -> {
            Resident r = residentRepository.findById(o.getResidentId()).orElse(null);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", o.getId());
            row.put("residentId", o.getResidentId());
            row.put("residentName", o.getResidentName());
            row.put("building", r != null ? r.getBuilding() : "");
            row.put("unit", r != null ? r.getUnit() : "");
            row.put("period", o.getPeriod());
            row.put("waterFee", o.getWaterFee());
            row.put("electricFee", o.getElectricFee());
            row.put("total", o.getTotal());
            row.put("status", o.getStatus());
            row.put("payTime", o.getPayTime());
            row.put("createTime", o.getCreateTime());
            return row;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> adminDashboard() {
        List<PaymentOrder> orders = orderRepository.findAll();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("residentCount", residentRepository.count());
        data.put("pendingOrders", orders.stream().filter(o -> BizConstants.STATUS_PENDING.equals(o.getStatus())).count());
        data.put("meterReadingCount", meterReadingRepository.count());
        double totalPaid = orders.stream()
                .filter(o -> BizConstants.STATUS_PAID.equals(o.getStatus()))
                .mapToDouble(o -> safe(o.getTotal()))
                .sum();
        data.put("totalPaid", fmt(totalPaid));
        data.put("recentOrders", orders.stream().limit(5).collect(Collectors.toList()));
        return data;
    }

    public Map<String, Object> residentDashboard(String residentId) {
        List<PaymentOrder> orders = orderRepository.search(residentId, null, null);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("pendingCount", orders.stream().filter(o -> BizConstants.STATUS_PENDING.equals(o.getStatus())).count());
        data.put("paidCount", orders.stream().filter(o -> BizConstants.STATUS_PAID.equals(o.getStatus())).count());
        data.put("orders", orders);
        return data;
    }

    private boolean inRange(String period, String start, String end) {
        if (!StringUtils.hasText(start) && !StringUtils.hasText(end)) {
            return true;
        }
        if (!StringUtils.hasText(period)) {
            return false;
        }
        if (StringUtils.hasText(start) && period.compareTo(start) < 0) {
            return false;
        }
        return !StringUtils.hasText(end) || period.compareTo(end) <= 0;
    }

    private Map<String, Object> formatAmounts(Map<String, Object> row) {
        row.put("totalAmount", fmt((double) row.get("totalAmount")));
        row.put("paidAmount", fmt((double) row.get("paidAmount")));
        return row;
    }

    private double safe(Double v) {
        return v != null ? v : 0;
    }

    private String fmt(double v) {
        return String.format("%.2f", v);
    }
}
