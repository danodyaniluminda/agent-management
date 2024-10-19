package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.Currency;
import com.biapay.agentmanagement.web.dto.packagemanagement.CurrencyDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CurrencyMapper {

    Currency fromCurrencyDtoToCurrency(CurrencyDto currencyDto);

    CurrencyDto fromCurrencyToCurrencyDto(Currency currency);
}