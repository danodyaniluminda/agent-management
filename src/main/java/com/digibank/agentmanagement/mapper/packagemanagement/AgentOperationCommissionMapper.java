package com.digibank.agentmanagement.mapper.packagemanagement;

import com.digibank.agentmanagement.domain.AgentOperationCommission;
import com.digibank.agentmanagement.web.dto.commission.AgentOperationCommissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgentOperationCommissionMapper {

  AgentOperationCommission fromOperationDtoToOperation(AgentOperationCommissionDto operationDto);

  AgentOperationCommissionDto fromOperationToOperationDto(AgentOperationCommission operation);
}
