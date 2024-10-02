package com.digibank.agentmanagement.mapper.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Operation;
import com.digibank.agentmanagement.web.dto.packagemanagement.OperationDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperationMapper {

    Operation fromOperationDtoToOperation(OperationDto operationDto);

    OperationDto fromOperationToOperationDto(Operation operation);
}