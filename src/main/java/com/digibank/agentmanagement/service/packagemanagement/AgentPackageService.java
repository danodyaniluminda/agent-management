package com.digibank.agentmanagement.service.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.AgentPackage;
import com.digibank.agentmanagement.web.dto.packagemanagement.AgentPackageDto;

import java.util.List;

public interface AgentPackageService {

    AgentPackage find(Long assetId);

    AgentPackage find(String assetId);

    AgentPackage findByName(String name);

    List<AgentPackageDto> getAll();

    AgentPackageDto get(Long packageId);

    AgentPackageDto add(AgentPackageDto agentPackageRequest);

    AgentPackageDto update(Long packageId, AgentPackageDto agentPackageRequest);

    void delete(Long packageId);

    void delete(String packageId);

    void updateStatus(Long packageId, Boolean active);
}
