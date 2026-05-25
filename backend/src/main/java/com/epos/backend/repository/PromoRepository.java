package com.epos.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.enums.PromoApplyType;
import com.epos.backend.enums.PromoStatus;
import com.epos.backend.model.entity.Promo;

import jakarta.persistence.LockModeType;

public interface PromoRepository extends JpaRepository<Promo, Long>, JpaSpecificationExecutor<Promo> {

    public Optional<Promo> findByPromoNo(String promoNo);
    public Optional<Promo> findByPromoCodeAndStatus(String promoCode, PromoStatus status);
    
    @Query("""
            SELECT DISTINCT p
            FROM Promo p
            WHERE p.status = :status
                AND p.applyType = :applyType
                AND :now BETWEEN p.startDate and p.endDate
            ORDER BY p.priority DESC, p.id ASC
            """)
    public List<Promo> findActivePromos(@Param("status") PromoStatus status, @Param("applyType") PromoApplyType applyType, @Param("now") LocalDateTime now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Promo p WHERE p.id = :id")
    public Optional<Promo> findByIdForUpdate(@Param("id") Long id);

}
