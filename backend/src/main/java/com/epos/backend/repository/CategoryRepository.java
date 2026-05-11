package com.epos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Boolean existsByName(String name);

}
