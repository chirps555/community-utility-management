package com.example.sdfguanlixt.dto;

public class TierDetail {

    private String tier;
    private double usage;
    private double unitPrice;
    private double fee;

    public TierDetail() {
    }

    public TierDetail(String tier, double usage, double unitPrice, double fee) {
        this.tier = tier;
        this.usage = usage;
        this.unitPrice = unitPrice;
        this.fee = fee;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public double getUsage() {
        return usage;
    }

    public void setUsage(double usage) {
        this.usage = usage;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
