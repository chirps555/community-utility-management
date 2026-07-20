package com.example.sdfguanlixt.dto;

import com.example.sdfguanlixt.entity.MeterReading;
import com.example.sdfguanlixt.entity.UsageFlow;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MeterReadingResult {

    @JsonProperty("meterReading")
    private MeterReading meterReading;

    @JsonProperty("usageFlows")
    private List<UsageFlow> usageFlows;

    public MeterReadingResult() {
    }

    public MeterReadingResult(MeterReading meterReading, List<UsageFlow> usageFlows) {
        this.meterReading = meterReading;
        this.usageFlows = usageFlows;
    }

    public MeterReading getMeterReading() {
        return meterReading;
    }

    public void setMeterReading(MeterReading meterReading) {
        this.meterReading = meterReading;
    }

    public List<UsageFlow> getUsageFlows() {
        return usageFlows;
    }

    public void setUsageFlows(List<UsageFlow> usageFlows) {
        this.usageFlows = usageFlows;
    }
}
