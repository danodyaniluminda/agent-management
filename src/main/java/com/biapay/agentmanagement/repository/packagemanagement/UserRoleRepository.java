package com.biapay.agentmanagement.repository.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByName(String name);

    Optional<UserRole> findByNameAndUserRoleIdNot(String name, Long userRoleId);
}
