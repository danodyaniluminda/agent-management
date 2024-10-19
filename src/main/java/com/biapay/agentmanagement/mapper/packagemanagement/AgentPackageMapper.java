package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.AgentPackage;
import com.biapay.agentmanagement.web.dto.packagemanagement.AgentPackageDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgentPackageMapper {

    AgentPackage fromAgentPackageDtoToAgentPackage(AgentPackageDto agentPackageDto);

    AgentPackageDto fromAgentPackageToAgentPackageDto(AgentPackage agentPackage);
}