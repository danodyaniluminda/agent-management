package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Currency;
import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.exception.ObjectAlreadyExistException;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.mapper.packagemanagement.CurrencyMapper;
import com.digibank.agentmanagement.repository.packagemanagement.CurrencyRepository;
import com.digibank.agentmanagement.service.packagemanagement.CurrencyService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.packagemanagement.CurrencyDto;
import com.digibank.agentmanagement.utils.ApiError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Override
    public Currency find(Long currencyId) {
        AgentManagementUtils.isValidId(currencyId);
        return currencyRepository.findById(currencyId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public Currency find(String currencyId) {
        try {
            return find(Long.parseLong(currencyId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public Currency findByName(String name) {
        return currencyRepository.findByName(name)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public Currency findByCode(String name) {
        return currencyRepository.findByCode(name)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public CurrencyDto get(Long currencyId) {
        AgentManagementUtils.isValidId(currencyId);
        Currency currency = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        return currencyMapper.fromCurrencyToCurrencyDto(currency);
    }

    @Override
    public List<CurrencyDto> getAll() {
        List<CurrencyDto> currencies = new ArrayList<>();
        currencyRepository.findAll().forEach(currency -> {
            CurrencyDto currencyDto = currencyMapper.fromCurrencyToCurrencyDto(currency);
            currencies.add(currencyDto);
        });
        return currencies;
    }

    @Override
    public CurrencyDto add(CurrencyDto currencyRequest) {
        if(currencyRepository.findByName(currencyRequest.getName()).isPresent()){
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, currencyRequest.getName());
        }

        Currency currency = currencyMapper.fromCurrencyDtoToCurrency(currencyRequest);
        currency.setCreateDate(Instant.now());
        currencyRepository.save(currency);
        return currencyMapper.fromCurrencyToCurrencyDto(currency);
    }

    @Override
    public CurrencyDto update(Long currencyId, CurrencyDto currencyRequest) {
        if(currencyRepository.findByNameAndCurrencyIdNot(currencyRequest.getName(), currencyId).isPresent()){
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, currencyRequest.getName());
        }

        Currency currency = find(currencyId);
        currency.setName(currencyRequest.getName());
        currency.setCode(currencyRequest.getCode());
        currency.setSymbol(currencyRequest.getSymbol());
        currency.setRate(currencyRequest.getRate());
        currencyRepository.save(currency);
        return currencyMapper.fromCurrencyToCurrencyDto(currency);
    }

    @Override
    public void delete(Long currencyId) {
        AgentManagementUtils.isValidId(currencyId);
        Currency currency = find(currencyId);
        currencyRepository.delete(currency);
    }

    @Override
    public void delete(String currencyId) {
        try {
            delete(Long.parseLong(currencyId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }
}
