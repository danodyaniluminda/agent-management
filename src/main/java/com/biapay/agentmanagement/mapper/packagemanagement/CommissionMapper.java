package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.Commission;
import com.biapay.agentmanagement.web.dto.packagemanagement.CommissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommissionMapper {

    Commission fromCommissionDtoToCommission(CommissionDto commissionDto);

    CommissionDto fromCommissionToCommissionDto(Commission commission);
}