package com.epos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.epos.backend.model.entity.TrxStockMovement;

public interface TrxStockMovementRepository extends JpaRepository<TrxStockMovement, Long>, JpaSpecificationExecutor<TrxStockMovement> {

}
