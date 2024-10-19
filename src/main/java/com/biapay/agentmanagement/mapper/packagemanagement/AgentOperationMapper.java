package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.AgentOperation;
import com.biapay.agentmanagement.web.dto.AgentOperationDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgentOperationMapper {

  AgentOperation fromOperationDtoToOperation(AgentOperationDto operationDto);

  AgentOperationDto fromOperationToOperationDto(AgentOperation operation);
}
