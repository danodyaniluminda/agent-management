package com.digibank.agentmanagement.repository.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.AgentPackage;
import com.digibank.agentmanagement.domain.packagemanagement.Operation;
import com.digibank.agentmanagement.domain.packagemanagement.PackageOperationPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageAssetOperationPermissionRepository extends JpaRepository<PackageOperationPermission, Long> {

    List<PackageOperationPermission> findAllByAgentPackage(AgentPackage agentPackage);

    void deleteByAgentPackage(AgentPackage agentPackage);

    Optional<PackageOperationPermission> findByOperationAndAgentPackage(Operation operation, AgentPackage agentPackage);
}
