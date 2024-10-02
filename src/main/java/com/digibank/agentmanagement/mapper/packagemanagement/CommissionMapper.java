package com.digibank.agentmanagement.mapper.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Commission;
import com.digibank.agentmanagement.web.dto.packagemanagement.CommissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommissionMapper {

    Commission fromCommissionDtoToCommission(CommissionDto commissionDto);

    CommissionDto fromCommissionToCommissionDto(Commission commission);
}