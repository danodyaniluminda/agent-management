package com.digibank.agentmanagement.mapper.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Currency;
import com.digibank.agentmanagement.web.dto.packagemanagement.CurrencyDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CurrencyMapper {

    Currency fromCurrencyDtoToCurrency(CurrencyDto currencyDto);

    CurrencyDto fromCurrencyToCurrencyDto(Currency currency);
}