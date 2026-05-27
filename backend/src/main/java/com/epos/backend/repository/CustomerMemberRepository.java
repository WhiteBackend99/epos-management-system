package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.model.entity.CustomerMember;

import jakarta.persistence.LockModeType;

public interface CustomerMemberRepository extends JpaRepository<CustomerMember, Long>, JpaSpecificationExecutor<CustomerMember> {

    public boolean existsByPhoneNumber(String phoneNumber);
    public boolean existsByEmail(String email);
    public Optional<CustomerMember> findByMemberCode(String memberCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT cm
            FROM CustomerMember cm
            WHERE cm.id = :id
            """)
    public Optional<CustomerMember> findByIdForUpdate(@Param("id") long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT cm
            FROM CustomerMember cm
            WHERE cm.memberCode = :memberCode
            """)
    public Optional<CustomerMember> findByMemberCodeForUpdate(@Param("memberCode") String memberCode);
}
