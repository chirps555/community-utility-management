package com.example.sdfguanlixt.service;

import com.example.sdfguanlixt.common.BizConstants;
import com.example.sdfguanlixt.common.BusinessException;
import com.example.sdfguanlixt.common.IdUtil;
import com.example.sdfguanlixt.entity.Utility;
import com.example.sdfguanlixt.repository.ResidentRepository;
import com.example.sdfguanlixt.repository.UtilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilityService {

    private final UtilityRepository utilityRepository;
    private final ResidentRepository residentRepository;

    public UtilityService(UtilityRepository utilityRepository, ResidentRepository residentRepository) {
        this.utilityRepository = utilityRepository;
        this.residentRepository = residentRepository;
    }

    public List<Utility> list(String residentId) {
        List<Utility> list = StringUtils.hasText(residentId)
                ? utilityRepository.findByResidentId(residentId)
                : utilityRepository.findAll();
        return list.stream().sorted(Comparator.comparing(Utility::getId)).collect(Collectors.toList());
    }

    public Utility getById(String id) {
        return utilityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, BizConstants.MSG_UTILITY_NOT_FOUND));
    }

    public Utility create(Utility utility) {
        if (!StringUtils.hasText(utility.getId())) {
            utility.setId(IdUtil.genId("U"));
        }
        fillResidentName(utility);
        return utilityRepository.save(utility);
    }

    public Utility update(String id, Utility data) {
        Utility existing = getById(id);
        if (StringUtils.hasText(data.getResidentId())) {
            existing.setResidentId(data.getResidentId());
        }
        if (data.getWaterPrice() != null) {
            existing.setWaterPrice(data.getWaterPrice());
        }
        if (data.getElectricPrice() != null) {
            existing.setElectricPrice(data.getElectricPrice());
        }
        if (data.getWaterBase() != null) {
            existing.setWaterBase(data.getWaterBase());
        }
        if (data.getElectricBase() != null) {
            existing.setElectricBase(data.getElectricBase());
        }
        if (data.getRemark() != null) {
            existing.setRemark(data.getRemark());
        }
        if (StringUtils.hasText(data.getResidentName())) {
            existing.setResidentName(data.getResidentName());
        } else {
            fillResidentName(existing);
        }
        return utilityRepository.save(existing);
    }

    public void delete(String id) {
        if (!utilityRepository.existsById(id)) {
            throw new BusinessException(404, BizConstants.MSG_UTILITY_NOT_FOUND);
        }
        utilityRepository.deleteById(id);
    }

    private void fillResidentName(Utility utility) {
        if (StringUtils.hasText(utility.getResidentName())) {
            return;
        }
        residentRepository.findById(utility.getResidentId())
                .ifPresent(r -> utility.setResidentName(r.getName()));
    }
}
