package com.epos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.TrxPurchaseDetail;

public interface TrxPurchaseDetailRepository extends JpaRepository<TrxPurchaseDetail, Long> {

}
