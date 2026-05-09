package com.epos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epos.backend.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByUsername(String username);
    
    public Boolean existsByUsername(String username);
}
