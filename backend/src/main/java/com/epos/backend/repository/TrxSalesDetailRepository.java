package com.epos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.TrxSalesDetail;

public interface TrxSalesDetailRepository extends JpaRepository<TrxSalesDetail, Long> {

}
