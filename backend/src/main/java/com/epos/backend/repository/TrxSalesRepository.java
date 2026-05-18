package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.TrxSales;

public interface TrxSalesRepository extends JpaRepository<TrxSales, Long> {

    public Optional<TrxSales> findBySalesNo(String salesNo);
    public Page<TrxSales> findBySalesNoContainingIgnoreCaseOrCustomerNameContainingIgnoreCase(String salesNo, String customerName, Pageable pageable);
    
}
