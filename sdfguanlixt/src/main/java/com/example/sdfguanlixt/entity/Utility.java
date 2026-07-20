package com.example.sdfguanlixt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "utility")
public class Utility {

    @Id
    private String id;

    @Column(name = "resident_id")
    @JsonProperty("residentId")
    private String residentId;

    @Column(name = "resident_name")
    @JsonProperty("residentName")
    private String residentName;

    @Column(name = "water_price")
    @JsonProperty("waterPrice")
    private Double waterPrice;

    @Column(name = "electric_price")
    @JsonProperty("electricPrice")
    private Double electricPrice;

    @Column(name = "water_base")
    @JsonProperty("waterBase")
    private Double waterBase;

    @Column(name = "electric_base")
    @JsonProperty("electricBase")
    private Double electricBase;

    private String remark;

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

    public Double getWaterPrice() {
        return waterPrice;
    }

    public void setWaterPrice(Double waterPrice) {
        this.waterPrice = waterPrice;
    }

    public Double getElectricPrice() {
        return electricPrice;
    }

    public void setElectricPrice(Double electricPrice) {
        this.electricPrice = electricPrice;
    }

    public Double getWaterBase() {
        return waterBase;
    }

    public void setWaterBase(Double waterBase) {
        this.waterBase = waterBase;
    }

    public Double getElectricBase() {
        return electricBase;
    }

    public void setElectricBase(Double electricBase) {
        this.electricBase = electricBase;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
