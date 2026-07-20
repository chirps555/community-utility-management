package com.example.sdfguanlixt.service;

import com.example.sdfguanlixt.common.BizConstants;
import com.example.sdfguanlixt.common.BusinessException;
import com.example.sdfguanlixt.common.IdUtil;
import com.example.sdfguanlixt.config.JwtUtil;
import com.example.sdfguanlixt.dto.LoginRequest;
import com.example.sdfguanlixt.dto.LoginResponse;
import com.example.sdfguanlixt.dto.RegisterRequest;
import com.example.sdfguanlixt.entity.Resident;
import com.example.sdfguanlixt.entity.SysUser;
import com.example.sdfguanlixt.entity.Utility;
import com.example.sdfguanlixt.repository.ResidentRepository;
import com.example.sdfguanlixt.repository.SysUserRepository;
import com.example.sdfguanlixt.repository.UtilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private static final double DEFAULT_WATER_PRICE = 3.5;
    private static final double DEFAULT_ELECTRIC_PRICE = 0.62;

    private final SysUserRepository sysUserRepository;
    private final ResidentRepository residentRepository;
    private final UtilityRepository utilityRepository;
    private final JwtUtil jwtUtil;

    public AuthService(
            SysUserRepository sysUserRepository,
            ResidentRepository residentRepository,
            UtilityRepository utilityRepository,
            JwtUtil jwtUtil) {
        this.sysUserRepository = sysUserRepository;
        this.residentRepository = residentRepository;
        this.utilityRepository = utilityRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserRepository
                .findByUsernameAndPasswordAndRole(
                        request.getUsername(),
                        request.getPassword(),
                        request.getRole().toUpperCase())
                .orElseThrow(() -> new BusinessException(401, BizConstants.MSG_LOGIN_FAILED));

        return buildLoginResponse(user);
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        String phone = request.getPhone().trim();
        String name = request.getName().trim();
        String building = request.getBuilding().trim();
        String unit = request.getUnit().trim();
        String type = request.getType().trim();
        String moveInDate = request.getMoveInDate().trim();

        if (sysUserRepository.findByUsername(phone).isPresent()) {
            throw new BusinessException(400, BizConstants.MSG_PHONE_REGISTERED);
        }

        Resident resident = residentRepository
                .findByBuildingAndUnitAndName(building, unit, name)
                .orElseGet(() -> createResident(name, phone, building, unit, type, moveInDate));

        if (sysUserRepository.findByResidentIdAndRole(resident.getId(), BizConstants.ROLE_RESIDENT).isPresent()) {
            throw new BusinessException(400, BizConstants.MSG_RESIDENT_HAS_ACCOUNT);
        }

        if (StringUtils.hasText(resident.getPhone()) && !phone.equals(resident.getPhone())) {
            throw new BusinessException(400, BizConstants.MSG_RESIDENT_INFO_MISMATCH);
        }

        resident.setPhone(phone);
        if (!StringUtils.hasText(resident.getType())) {
            resident.setType(type);
        }
        if (!StringUtils.hasText(resident.getMoveInDate())) {
            resident.setMoveInDate(moveInDate);
        }
        residentRepository.save(resident);

        ensureUtilityExists(resident);

        SysUser user = new SysUser();
        user.setId(IdUtil.genId("u"));
        user.setUsername(phone);
        user.setPassword(request.getPassword());
        user.setRole(BizConstants.ROLE_RESIDENT);
        user.setResidentId(resident.getId());
        user.setName(resident.getName());
        sysUserRepository.save(user);

        return buildLoginResponse(user);
    }

    private Resident createResident(
            String name, String phone, String building, String unit, String type, String moveInDate) {
        var occupants = residentRepository.findByBuildingAndUnit(building, unit);
        if (!occupants.isEmpty()) {
            throw new BusinessException(400, BizConstants.MSG_ADDRESS_OCCUPIED);
        }

        Resident resident = new Resident();
        resident.setId(IdUtil.genId("R"));
        resident.setName(name);
        resident.setPhone(phone);
        resident.setBuilding(building);
        resident.setUnit(unit);
        resident.setType(type);
        resident.setMoveInDate(moveInDate);
        return residentRepository.save(resident);
    }

    private void ensureUtilityExists(Resident resident) {
        if (utilityRepository.findFirstByResidentId(resident.getId()).isPresent()) {
            return;
        }
        Utility utility = new Utility();
        utility.setId(IdUtil.genId("U"));
        utility.setResidentId(resident.getId());
        utility.setResidentName(resident.getName());
        utility.setWaterPrice(DEFAULT_WATER_PRICE);
        utility.setElectricPrice(DEFAULT_ELECTRIC_PRICE);
        utility.setWaterBase(0.0);
        utility.setElectricBase(0.0);
        utility.setRemark("");
        utilityRepository.save(utility);
    }

    private LoginResponse buildLoginResponse(SysUser user) {
        String token = jwtUtil.generateToken(
                user.getId(),
                user.getRole(),
                user.getResidentId(),
                user.getName());
        return new LoginResponse(token, user.getResidentId(), user.getName(), user.getRole());
    }
}
