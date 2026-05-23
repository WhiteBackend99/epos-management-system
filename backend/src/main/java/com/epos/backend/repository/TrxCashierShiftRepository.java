package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.epos.backend.enums.CashierShiftStatus;
import com.epos.backend.model.entity.TrxCashierShift;

public interface TrxCashierShiftRepository extends JpaRepository<TrxCashierShift, Long>, JpaSpecificationExecutor<TrxCashierShift> {

    public Optional<TrxCashierShift> findByShiftNo(String shiftNo);
    public Optional<TrxCashierShift> findFirstByCashierUsernameAndStatusOrderByOpenedAtDesc(String cashierUsername, CashierShiftStatus status);
    public boolean existsByCashierUsernameAndStatus(String cashierUsername, CashierShiftStatus status);
}
