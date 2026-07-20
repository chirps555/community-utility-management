package com.example.sdfguanlixt.service;

import com.example.sdfguanlixt.common.BizConstants;
import com.example.sdfguanlixt.common.BusinessException;
import com.example.sdfguanlixt.common.IdUtil;
import com.example.sdfguanlixt.entity.Resident;
import com.example.sdfguanlixt.repository.MeterReadingRepository;
import com.example.sdfguanlixt.repository.PaymentOrderRepository;
import com.example.sdfguanlixt.repository.ResidentRepository;
import com.example.sdfguanlixt.repository.UsageFlowRepository;
import com.example.sdfguanlixt.repository.UtilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResidentService {

    private final ResidentRepository residentRepository;
    private final UtilityRepository utilityRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final UsageFlowRepository usageFlowRepository;
    private final PaymentOrderRepository paymentOrderRepository;

    public ResidentService(
            ResidentRepository residentRepository,
            UtilityRepository utilityRepository,
            MeterReadingRepository meterReadingRepository,
            UsageFlowRepository usageFlowRepository,
            PaymentOrderRepository paymentOrderRepository) {
        this.residentRepository = residentRepository;
        this.utilityRepository = utilityRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.usageFlowRepository = usageFlowRepository;
        this.paymentOrderRepository = paymentOrderRepository;
    }

    public List<Resident> list(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return residentRepository.findAll().stream()
                    .sorted(Comparator.comparing(Resident::getId))
                    .collect(Collectors.toList());
        }
        String k = keyword.trim();
        return residentRepository
                .findByNameContainingOrPhoneContainingOrBuildingContaining(k, k, k)
                .stream()
                .sorted(Comparator.comparing(Resident::getId))
                .collect(Collectors.toList());
    }

    public Resident getById(String id) {
        return residentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, BizConstants.MSG_RESIDENT_NOT_FOUND));
    }

    public Resident create(Resident resident) {
        if (!StringUtils.hasText(resident.getId())) {
            resident.setId(IdUtil.genId("R"));
        }
        return residentRepository.save(resident);
    }

    public Resident update(String id, Resident data) {
        Resident existing = getById(id);
        if (StringUtils.hasText(data.getName())) {
            existing.setName(data.getName());
        }
        if (StringUtils.hasText(data.getPhone())) {
            existing.setPhone(data.getPhone());
        }
        if (StringUtils.hasText(data.getBuilding())) {
            existing.setBuilding(data.getBuilding());
        }
        if (StringUtils.hasText(data.getUnit())) {
            existing.setUnit(data.getUnit());
        }
        if (StringUtils.hasText(data.getType())) {
            existing.setType(data.getType());
        }
        if (StringUtils.hasText(data.getMoveInDate())) {
            existing.setMoveInDate(data.getMoveInDate());
        }
        Resident saved = residentRepository.save(existing);
        utilityRepository.findByResidentId(id).forEach(u -> {
            u.setResidentName(saved.getName());
            utilityRepository.save(u);
        });
        return saved;
    }

    @Transactional
    public void delete(String id) {
        if (!residentRepository.existsById(id)) {
            throw new BusinessException(404, BizConstants.MSG_RESIDENT_NOT_FOUND);
        }
        utilityRepository.deleteByResidentId(id);
        meterReadingRepository.deleteByResidentId(id);
        usageFlowRepository.deleteByResidentId(id);
        paymentOrderRepository.deleteByResidentId(id);
        residentRepository.deleteById(id);
    }
}
