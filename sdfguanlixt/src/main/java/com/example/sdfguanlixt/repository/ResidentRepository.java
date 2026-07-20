package com.example.sdfguanlixt.repository;

import com.example.sdfguanlixt.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResidentRepository extends JpaRepository<Resident, String> {

    List<Resident> findByNameContainingOrPhoneContainingOrBuildingContaining(
            String name, String phone, String building);

    Optional<Resident> findByBuildingAndUnitAndName(String building, String unit, String name);

    List<Resident> findByBuildingAndUnit(String building, String unit);
}
