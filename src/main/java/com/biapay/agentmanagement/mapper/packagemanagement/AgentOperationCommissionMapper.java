package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.AgentOperationCommission;
import com.biapay.agentmanagement.web.dto.commission.AgentOperationCommissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgentOperationCommissionMapper {

  AgentOperationCommission fromOperationDtoToOperation(AgentOperationCommissionDto operationDto);

  AgentOperationCommissionDto fromOperationToOperationDto(AgentOperationCommission operation);
}
