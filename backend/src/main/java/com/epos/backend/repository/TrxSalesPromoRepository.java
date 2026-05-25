package com.epos.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.TrxSalesPromo;

public interface TrxSalesPromoRepository extends JpaRepository<TrxSalesPromo, Long> {

    public List<TrxSalesPromo> findBySalesId(Long salesId);
}
