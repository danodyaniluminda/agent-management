package com.digibank.agentmanagement.service.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Currency;
import com.digibank.agentmanagement.web.dto.packagemanagement.CurrencyDto;

import java.util.List;

public interface CurrencyService {

    Currency find(Long currencyId);

    Currency find(String currencyId);

    Currency findByName(String name);

    Currency findByCode(String name);

    List<CurrencyDto> getAll();

    CurrencyDto get(Long currencyId);

    CurrencyDto add(CurrencyDto currencyRequest);

    CurrencyDto update(Long currencyId, CurrencyDto currencyRequest);

    void delete(Long currencyId);

    void delete(String currencyId);
}
