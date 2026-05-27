package com.epos.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.model.entity.TrxLoyaltyPointBucket;

import jakarta.persistence.LockModeType;

public interface TrxLoyaltyPointBucketRepository extends JpaRepository<TrxLoyaltyPointBucket, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT tlpb
            FROM TrxLoyaltyPointBucket tlpb
            WHERE tlpb.customerMember.id = :memberId
            AND tlpb.remainingPoint > 0
            AND tlpb.expiredAt > CURRENT_TIMESTAMP
            ORDER BY tlpb.expiredAt ASC, tlpb.id ASC
        """)
    public List<TrxLoyaltyPointBucket> findActiveBucketsForRedeem(@Param("memberId") Long memberId);

}
