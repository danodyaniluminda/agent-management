package com.digibank.agentmanagement.service.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.AgentPackage;
import com.digibank.agentmanagement.domain.packagemanagement.Operation;
import com.digibank.agentmanagement.domain.packagemanagement.PackageOperationPermission;
import com.digibank.agentmanagement.web.dto.packagemanagement.requests.AssetOperationPermissionDto;

import java.util.List;

public interface PackageOperationPermissionService {

    void addAll(AgentPackage agentPackage, List<AssetOperationPermissionDto> assetOperationPermissions);

    List<AssetOperationPermissionDto> getAllByAgentPackage(AgentPackage agentPackage);

    List<PackageOperationPermission> findAllByAgentPackage(AgentPackage agentPackage);

    void updateAll(AgentPackage agentPackage, List<AssetOperationPermissionDto> assetOperationPermissions);

    PackageOperationPermission checkPermissionsForOperationInPackage(Operation operation, AgentPackage agentPackage);
}
