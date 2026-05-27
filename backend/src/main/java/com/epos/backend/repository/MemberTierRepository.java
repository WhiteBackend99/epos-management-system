package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.MemberTier;

public interface MemberTierRepository extends JpaRepository<MemberTier, Long> {

    public Optional<MemberTier> findByTierCodeAndActiveFlagTrue(String tierCode);
    
}
