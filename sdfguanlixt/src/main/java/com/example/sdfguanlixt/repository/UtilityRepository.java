package com.example.sdfguanlixt.repository;

import com.example.sdfguanlixt.entity.Utility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilityRepository extends JpaRepository<Utility, String> {

    List<Utility> findByResidentId(String residentId);

    Optional<Utility> findFirstByResidentId(String residentId);

    void deleteByResidentId(String residentId);
}
