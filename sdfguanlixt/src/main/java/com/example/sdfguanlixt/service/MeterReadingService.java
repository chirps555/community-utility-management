package com.example.sdfguanlixt.service;

import com.example.sdfguanlixt.common.BizConstants;
import com.example.sdfguanlixt.common.BusinessException;
import com.example.sdfguanlixt.common.IdUtil;
import com.example.sdfguanlixt.common.TimeUtil;
import com.example.sdfguanlixt.dto.MeterReadingRequest;
import com.example.sdfguanlixt.dto.MeterReadingResult;
import com.example.sdfguanlixt.entity.MeterReading;
import com.example.sdfguanlixt.entity.Resident;
import com.example.sdfguanlixt.entity.UsageFlow;
import com.example.sdfguanlixt.entity.Utility;
import com.example.sdfguanlixt.repository.MeterReadingRepository;
import com.example.sdfguanlixt.repository.ResidentRepository;
import com.example.sdfguanlixt.repository.UsageFlowRepository;
import com.example.sdfguanlixt.repository.UtilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final UsageFlowRepository usageFlowRepository;
    private final UtilityRepository utilityRepository;
    private final ResidentRepository residentRepository;

    public MeterReadingService(
            MeterReadingRepository meterReadingRepository,
            UsageFlowRepository usageFlowRepository,
            UtilityRepository utilityRepository,
            ResidentRepository residentRepository) {
        this.meterReadingRepository = meterReadingRepository;
        this.usageFlowRepository = usageFlowRepository;
        this.utilityRepository = utilityRepository;
        this.residentRepository = residentRepository;
    }

    public List<MeterReading> list(String residentId, String period) {
        List<MeterReading> list;
        if (StringUtils.hasText(residentId) && StringUtils.hasText(period)) {
            list = meterReadingRepository.findByResidentIdAndPeriod(residentId, period);
        } else if (StringUtils.hasText(residentId)) {
            list = meterReadingRepository.findByResidentIdOrderByPeriodDesc(residentId);
        } else if (StringUtils.hasText(period)) {
            list = meterReadingRepository.findByPeriodOrderByReadDateDesc(period);
        } else {
            list = meterReadingRepository.findAll();
        }
        return list.stream()
                .sorted(Comparator.comparing(MeterReading::getPeriod).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public MeterReadingResult create(MeterReadingRequest req) {
        Resident resident = residentRepository.findById(req.getResidentId())
                .orElseThrow(() -> new BusinessException(404, BizConstants.MSG_RESIDENT_NOT_FOUND));

        String residentName = StringUtils.hasText(req.getResidentName())
                ? req.getResidentName() : resident.getName();

        Utility utility = utilityRepository.findFirstByResidentId(req.getResidentId()).orElse(null);
        double waterPrice = utility != null && utility.getWaterPrice() != null ? utility.getWaterPrice() : 3.5;
        double electricPrice = utility != null && utility.getElectricPrice() != null ? utility.getElectricPrice() : 0.62;

        double prevWater = resolvePrevWater(req, utility);
        double prevElectric = resolvePrevElectric(req, utility);

        double waterUsage = Math.max(0, req.getWaterReading() - prevWater);
        double electricUsage = Math.max(0, req.getElectricReading() - prevElectric);

        MeterReading reading = new MeterReading();
        reading.setId(IdUtil.genId("M"));
        reading.setResidentId(req.getResidentId());
        reading.setResidentName(residentName);
        reading.setPeriod(req.getPeriod());
        reading.setWaterReading(req.getWaterReading());
        reading.setElectricReading(req.getElectricReading());
        reading.setReadDate(StringUtils.hasText(req.getReadDate()) ? req.getReadDate() : TimeUtil.today());
        reading.setOperator(StringUtils.hasText(req.getOperator()) ? req.getOperator() : BizConstants.DEFAULT_OPERATOR);
        meterReadingRepository.save(reading);

        String createTime = TimeUtil.nowDateTime();
        List<UsageFlow> flows = new ArrayList<>();

        if (waterUsage > 0) {
            UsageFlow waterFlow = buildFlow(BizConstants.TYPE_WATER, waterUsage, waterPrice, req, residentName, createTime);
            usageFlowRepository.save(waterFlow);
            flows.add(waterFlow);
        }
        if (electricUsage > 0) {
            UsageFlow electricFlow = buildFlow(BizConstants.TYPE_ELECTRIC, electricUsage, electricPrice, req, residentName, createTime);
            usageFlowRepository.save(electricFlow);
            flows.add(electricFlow);
        }

        return new MeterReadingResult(reading, flows);
    }

    private double resolvePrevWater(MeterReadingRequest req, Utility utility) {
        if (req.getPrevWater() != null) {
            return req.getPrevWater();
        }
        return meterReadingRepository
                .findFirstByResidentIdOrderByPeriodDescReadDateDesc(req.getResidentId())
                .map(MeterReading::getWaterReading)
                .orElse(utility != null && utility.getWaterBase() != null ? utility.getWaterBase() : 0.0);
    }

    private double resolvePrevElectric(MeterReadingRequest req, Utility utility) {
        if (req.getPrevElectric() != null) {
            return req.getPrevElectric();
        }
        return meterReadingRepository
                .findFirstByResidentIdOrderByPeriodDescReadDateDesc(req.getResidentId())
                .map(MeterReading::getElectricReading)
                .orElse(utility != null && utility.getElectricBase() != null ? utility.getElectricBase() : 0.0);
    }

    private UsageFlow buildFlow(
            String type,
            double usage,
            double price,
            MeterReadingRequest req,
            String residentName,
            String createTime) {
        UsageFlow flow = new UsageFlow();
        flow.setId(IdUtil.genId("F"));
        flow.setResidentId(req.getResidentId());
        flow.setResidentName(residentName);
        flow.setType(type);
        flow.setUsage(round2(usage));
        flow.setPeriod(req.getPeriod());
        flow.setAmount(round2(usage * price));
        flow.setCreateTime(createTime);
        return flow;
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
