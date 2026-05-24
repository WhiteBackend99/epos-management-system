package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.enums.ReceiptReferenceType;
import com.epos.backend.model.entity.TrxReceipt;

public interface TrxReceiptRepository extends JpaRepository<TrxReceipt, Long> {

    public Optional<TrxReceipt> findByReceiptNo(String receiptNo);
    public Optional<TrxReceipt> findByReferenceTypeAndReferenceNo(ReceiptReferenceType referenceType, String referenceNo);
}
