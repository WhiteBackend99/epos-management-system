package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.epos.backend.model.entity.TrxSalesReturn;

public interface TrxSalesReturnRepository extends JpaRepository<TrxSalesReturn, Long>, JpaSpecificationExecutor<TrxSalesReturn> {

    public Optional<TrxSalesReturn> findByReturnNo(String returnNo);
    
}
