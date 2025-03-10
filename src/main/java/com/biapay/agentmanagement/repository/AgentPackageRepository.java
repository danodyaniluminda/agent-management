package com.biapay.agentmanagement.repository;

import com.biapay.agentmanagement.domain.AgentType;
import com.biapay.agentmanagement.domain.packagemanagement.AgentPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentPackageRepository extends JpaRepository<AgentPackage, Long> {

    Optional<AgentPackage> findByName(String name);

    Optional<AgentPackage> findByPackageId(Long packageId);

    Optional<AgentPackage> findByNameAndPackageIdNot(String name, Long packageId);

    Optional<AgentPackage> findByAgentTypeAndIsDefault(AgentType agentType , boolean isDefault);
}
