package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epos.backend.model.entity.Category;

import jakarta.persistence.LockModeType;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    public Boolean existsByNameIgnoreCaseAndIsDeletedFalse(String name);
    public Boolean existsByNameIgnoreCaseAndIdNotAndIsDeletedFalse(String name, Long id);
    public Optional<Category> findByIdAndIsDeletedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT c
            FROM Category c
            WHERE c.id = :id
            AND c.isDeleted = false
        """)
    public Optional<Category> findByIdForUpdate(@Param("id") Long id);

}
