package com.example.sdfguanlixt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MeterReadingRequest {

    @NotBlank
    @JsonProperty("residentId")
    private String residentId;

    @JsonProperty("residentName")
    private String residentName;

    @NotBlank
    private String period;

    @JsonProperty("prevWater")
    private Double prevWater;

    @NotNull
    @JsonProperty("waterReading")
    private Double waterReading;

    @JsonProperty("prevElectric")
    private Double prevElectric;

    @NotNull
    @JsonProperty("electricReading")
    private Double electricReading;

    @JsonProperty("readDate")
    private String readDate;

    private String operator;

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

    public Double getPrevWater() {
        return prevWater;
    }

    public void setPrevWater(Double prevWater) {
        this.prevWater = prevWater;
    }

    public Double getWaterReading() {
        return waterReading;
    }

    public void setWaterReading(Double waterReading) {
        this.waterReading = waterReading;
    }

    public Double getPrevElectric() {
        return prevElectric;
    }

    public void setPrevElectric(Double prevElectric) {
        this.prevElectric = prevElectric;
    }

    public Double getElectricReading() {
        return electricReading;
    }

    public void setElectricReading(Double electricReading) {
        this.electricReading = electricReading;
    }

    public String getReadDate() {
        return readDate;
    }

    public void setReadDate(String readDate) {
        this.readDate = readDate;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
