package com.example.sdfguanlixt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payment_order")
public class PaymentOrder {

    @Id
    private String id;

    @Column(name = "resident_id")
    @JsonProperty("residentId")
    private String residentId;

    @Column(name = "resident_name")
    @JsonProperty("residentName")
    private String residentName;

    private String period;

    @Column(name = "water_fee")
    @JsonProperty("waterFee")
    private Double waterFee;

    @Column(name = "electric_fee")
    @JsonProperty("electricFee")
    private Double electricFee;

    @Column(name = "total_amount")
    @JsonProperty("total")
    private Double total;

    private String status;

    @Column(name = "pay_time")
    @JsonProperty("payTime")
    private String payTime;

    @Column(name = "create_time")
    @JsonProperty("createTime")
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResidentId() {
        return residentId;
    }

    public void setResidentId(String residentId) {
        this.residentId = residentId;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getWaterFee() {
        return waterFee;
    }

    public void setWaterFee(Double waterFee) {
        this.waterFee = waterFee;
    }

    public Double getElectricFee() {
        return electricFee;
    }

    public void setElectricFee(Double electricFee) {
        this.electricFee = electricFee;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
