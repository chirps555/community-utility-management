package com.example.sdfguanlixt.repository;

import com.example.sdfguanlixt.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String> {

    void deleteByResidentId(String residentId);

    Optional<PaymentOrder> findByResidentIdAndPeriod(String residentId, String period);

    @Query("SELECT o FROM PaymentOrder o WHERE " +
            "(:residentId IS NULL OR o.residentId = :residentId) AND " +
            "(:period IS NULL OR o.period = :period) AND " +
            "(:status IS NULL OR o.status = :status) " +
            "ORDER BY o.createTime DESC")
    List<PaymentOrder> search(
            @Param("residentId") String residentId,
            @Param("period") String period,
            @Param("status") String status);
}
