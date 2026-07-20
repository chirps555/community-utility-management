package com.example.sdfguanlixt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class BillCalculationResult {

    @JsonProperty("residentId")
    private String residentId;

    @JsonProperty("residentName")
    private String residentName;

    private String period;
    private double waterUsage;
    private double electricUsage;
    private double waterFee;
    private double electricFee;
    private double total;
    private String algorithm;

    @JsonProperty("waterTiers")
    private List<TierDetail> waterTiers = new ArrayList<>();

    @JsonProperty("electricTiers")
    private List<TierDetail> electricTiers = new ArrayList<>();

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

    public double getWaterUsage() {
        return waterUsage;
    }

    public void setWaterUsage(double waterUsage) {
        this.waterUsage = waterUsage;
    }

    public double getElectricUsage() {
        return electricUsage;
    }

    public void setElectricUsage(double electricUsage) {
        this.electricUsage = electricUsage;
    }

    public double getWaterFee() {
        return waterFee;
    }

    public void setWaterFee(double waterFee) {
        this.waterFee = waterFee;
    }

    public double getElectricFee() {
        return electricFee;
    }

    public void setElectricFee(double electricFee) {
        this.electricFee = electricFee;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public List<TierDetail> getWaterTiers() {
        return waterTiers;
    }

    public void setWaterTiers(List<TierDetail> waterTiers) {
        this.waterTiers = waterTiers;
    }

    public List<TierDetail> getElectricTiers() {
        return electricTiers;
    }

    public void setElectricTiers(List<TierDetail> electricTiers) {
        this.electricTiers = electricTiers;
    }
}
