package com.example.sdfguanlixt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "meter_reading")
public class MeterReading {

    @Id
    private String id;

    @Column(name = "resident_id")
    @JsonProperty("residentId")
    private String residentId;

    @Column(name = "resident_name")
    @JsonProperty("residentName")
    private String residentName;

    private String period;

    @Column(name = "water_reading")
    @JsonProperty("waterReading")
    private Double waterReading;

    @Column(name = "electric_reading")
    @JsonProperty("electricReading")
    private Double electricReading;

    @Column(name = "read_date")
    @JsonProperty("readDate")
    private String readDate;

    private String operator;

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

    public Double getWaterReading() {
        return waterReading;
    }

    public void setWaterReading(Double waterReading) {
        this.waterReading = waterReading;
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
