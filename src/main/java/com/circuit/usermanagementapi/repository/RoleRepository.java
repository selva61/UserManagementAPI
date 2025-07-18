package com.circuit.usermanagementapi.repository;

import com.circuit.usermanagementapi.model.ERole;
import com.circuit.usermanagementapi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}