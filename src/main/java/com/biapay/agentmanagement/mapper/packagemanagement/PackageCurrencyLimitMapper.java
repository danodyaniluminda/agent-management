package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.PackageCurrencyLimit;
import com.biapay.agentmanagement.web.dto.packagemanagement.PackageCurrencyLimitDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PackageCurrencyLimitMapper {

    PackageCurrencyLimit fromPackageCurrencyLimitDtoToPackageCurrencyLimit(PackageCurrencyLimitDto packageCurrencyLimitDto);

    PackageCurrencyLimitDto fromPackageCurrencyLimitToPackageCurrencyLimitDto(PackageCurrencyLimit packageCurrencyLimit);
}