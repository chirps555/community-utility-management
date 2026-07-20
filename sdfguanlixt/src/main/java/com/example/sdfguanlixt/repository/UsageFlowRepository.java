package com.example.sdfguanlixt.repository;

import com.example.sdfguanlixt.entity.UsageFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsageFlowRepository extends JpaRepository<UsageFlow, String> {

    void deleteByResidentId(String residentId);

    @Query("SELECT u FROM UsageFlow u WHERE " +
            "(:residentId IS NULL OR u.residentId = :residentId) AND " +
            "(:type IS NULL OR u.type = :type) AND " +
            "(:period IS NULL OR u.period = :period) " +
            "ORDER BY u.createTime DESC")
    List<UsageFlow> search(
            @Param("residentId") String residentId,
            @Param("type") String type,
            @Param("period") String period);
}
