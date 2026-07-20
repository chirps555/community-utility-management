package com.example.sdfguanlixt.repository;

import com.example.sdfguanlixt.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeterReadingRepository extends JpaRepository<MeterReading, String> {

    List<MeterReading> findByResidentIdOrderByPeriodDesc(String residentId);

    List<MeterReading> findByResidentIdAndPeriod(String residentId, String period);

    List<MeterReading> findByPeriodOrderByReadDateDesc(String period);

    Optional<MeterReading> findFirstByResidentIdOrderByPeriodDescReadDateDesc(String residentId);

    void deleteByResidentId(String residentId);
}
