package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.epos.backend.enums.LoyaltyPointType;
import com.epos.backend.model.entity.TrxLoyaltyPointLedger;

public interface TrxLoyaltyPointLedgerRepository extends JpaRepository<TrxLoyaltyPointLedger, Long>, JpaSpecificationExecutor<TrxLoyaltyPointLedger> {

    public boolean existsBySalesNoAndPointType(String salesNo, LoyaltyPointType pointType);
    public Optional<TrxLoyaltyPointLedger> findBySalesNoAndPointType(String salesNo, LoyaltyPointType pointType);

}
