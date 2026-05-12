package com.epos.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.epos.backend.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    public boolean existsByNameAndIsDeletedFalse(String name);
    public boolean existsBySku(String sku);
    public Page<Product> findByIsDeletedFalse(Pageable pageable);

}
