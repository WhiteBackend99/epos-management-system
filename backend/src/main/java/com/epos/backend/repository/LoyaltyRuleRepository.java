package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epos.backend.model.entity.LoyaltyRule;

public interface LoyaltyRuleRepository extends JpaRepository<LoyaltyRule, Long> {

    @Query(value = """
            SELECT mlr.*
            FROM mst_loyalty_rule mlr
            WHERE mlr.active_flag = true
            ORDER BY mlr.id DESC
            LIMIT 1
        """, nativeQuery = true)
    public Optional<LoyaltyRule> findActiveRule();

}
