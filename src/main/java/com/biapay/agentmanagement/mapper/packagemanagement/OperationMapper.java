package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.Operation;
import com.biapay.agentmanagement.web.dto.packagemanagement.OperationDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperationMapper {

    Operation fromOperationDtoToOperation(OperationDto operationDto);

    OperationDto fromOperationToOperationDto(Operation operation);
}