package com.circuit.usermanagementapi.repository;

import com.circuit.usermanagementapi.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);
    
    Boolean existsByName(String name);
}