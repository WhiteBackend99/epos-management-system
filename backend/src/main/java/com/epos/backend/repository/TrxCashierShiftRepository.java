package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.enums.CashierShiftStatus;
import com.epos.backend.model.entity.TrxCashierShift;

import jakarta.persistence.LockModeType;

public interface TrxCashierShiftRepository extends JpaRepository<TrxCashierShift, Long>, JpaSpecificationExecutor<TrxCashierShift> {

    public Optional<TrxCashierShift> findByShiftNo(String shiftNo);
    public Optional<TrxCashierShift> findFirstByCashierUsernameAndStatusOrderByOpenedAtDesc(String cashierUsername, CashierShiftStatus status);
    public boolean existsByCashierUsernameAndStatus(String cashierUsername, CashierShiftStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT cs
            FROM TrxCashierShift cs
            WHERE cs.id = :id
        """)
    public Optional<TrxCashierShift> findByIdForUpdate(@Param("id") Long id);

}
