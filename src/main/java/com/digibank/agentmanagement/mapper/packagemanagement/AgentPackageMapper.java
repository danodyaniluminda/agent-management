package com.digibank.agentmanagement.mapper.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.AgentPackage;
import com.digibank.agentmanagement.web.dto.packagemanagement.AgentPackageDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgentPackageMapper {

    AgentPackage fromAgentPackageDtoToAgentPackage(AgentPackageDto agentPackageDto);

    AgentPackageDto fromAgentPackageToAgentPackageDto(AgentPackage agentPackage);
}