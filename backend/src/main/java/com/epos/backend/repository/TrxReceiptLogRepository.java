package com.epos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.TrxReceiptLog;

public interface TrxReceiptLogRepository extends JpaRepository<TrxReceiptLog, Long> {

}
