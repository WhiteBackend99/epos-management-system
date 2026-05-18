package com.epos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.TrxSalesPayment;

public interface TrxSalesPaymentRepository extends JpaRepository<TrxSalesPayment, Long> {

}
