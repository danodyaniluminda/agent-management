package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.AgentPackage;
import com.digibank.agentmanagement.domain.packagemanagement.Currency;
import com.digibank.agentmanagement.domain.packagemanagement.PackageCurrencyLimit;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.mapper.packagemanagement.PackageCurrencyLimitMapper;
import com.digibank.agentmanagement.repository.packagemanagement.PackageCurrencyLimitRepository;
import com.digibank.agentmanagement.service.packagemanagement.CurrencyService;
import com.digibank.agentmanagement.service.packagemanagement.PackageCurrencyLimitService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.dto.packagemanagement.PackageCurrencyLimitDto;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PackageCurrencyLimitServiceImpl implements PackageCurrencyLimitService {

    private final PackageCurrencyLimitRepository packageCurrencyLimitRepository;
    private final PackageCurrencyLimitMapper packageCurrencyLimitMapper;

    private final CurrencyService currencyService;

    @Override
    public List<PackageCurrencyLimitDto> getAllByAgentPackage(AgentPackage agentPackage) {
        List<PackageCurrencyLimitDto> packageCurrencyLimits = new ArrayList<>();
        packageCurrencyLimitRepository.findAllByAgentPackage(agentPackage).forEach(packageCurrencyLimit -> {
            PackageCurrencyLimitDto packageCurrencyLimitDto = packageCurrencyLimitMapper.
                    fromPackageCurrencyLimitToPackageCurrencyLimitDto(packageCurrencyLimit);
            packageCurrencyLimitDto.setCurrencyName(packageCurrencyLimitDto.getCurrency().getName());
            packageCurrencyLimits.add(packageCurrencyLimitDto);
        });
        return packageCurrencyLimits;
    }

    @Override
    public List<PackageCurrencyLimit> findAllByAgentPackage(AgentPackage agentPackage) {
        return packageCurrencyLimitRepository.findAllByAgentPackage(agentPackage);
    }

    @Override
    public PackageCurrencyLimit checkLimitsRegisteredForCurrencyInPackage(Currency currency, AgentPackage agentPackage) {
        return packageCurrencyLimitRepository.findByCurrencyAndAgentPackage(currency, agentPackage)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.PACKAGE_CURRENCY_NOT_AVAILABLE));
    }

    @Override
    public void addAll(AgentPackage agentPackage, List<PackageCurrencyLimitDto> packageCurrencyLimits) {
        if(CollectionUtils.isNotEmpty(packageCurrencyLimits)){
            packageCurrencyLimits.forEach(packageCurrencyLimitDto  -> {
                Currency currency = currencyService.findByName(packageCurrencyLimitDto.getCurrencyName());
                PackageCurrencyLimit packageCurrencyLimit = new PackageCurrencyLimit();
                packageCurrencyLimit.setDailyTransactionCount(packageCurrencyLimitDto.getDailyTransactionCount());
                packageCurrencyLimit.setWeeklyTransactionCount(packageCurrencyLimitDto.getWeeklyTransactionCount());
                packageCurrencyLimit.setMonthlyTransactionCount(packageCurrencyLimitDto.getMonthlyTransactionCount());
                packageCurrencyLimit.setDailyTransactionAmount(packageCurrencyLimitDto.getDailyTransactionAmount());
                packageCurrencyLimit.setWeeklyTransactionAmount(packageCurrencyLimitDto.getWeeklyTransactionAmount());
                packageCurrencyLimit.setMonthlyTransactionAmount(packageCurrencyLimitDto.getMonthlyTransactionAmount());

                packageCurrencyLimit.setDailySendingLimit(packageCurrencyLimitDto.getDailySendingLimit());
                packageCurrencyLimit.setDailyReceivingLimit(packageCurrencyLimitDto.getDailyReceivingLimit());
                packageCurrencyLimit.setWeeklySendingLimit(packageCurrencyLimitDto.getWeeklyReceivingLimit());
                packageCurrencyLimit.setWeeklyReceivingLimit(packageCurrencyLimitDto.getWeeklyReceivingLimit());
                packageCurrencyLimit.setMonthlySendingLimit(packageCurrencyLimitDto.getMonthlySendingLimit());
                packageCurrencyLimit.setMonthlyReceivingLimit(packageCurrencyLimitDto.getMonthlyReceivingLimit());

                packageCurrencyLimit.setCreateDate(Instant.now());
                packageCurrencyLimit.setCurrency(currency);
                packageCurrencyLimit.setAgentPackage(agentPackage);
                packageCurrencyLimitRepository.save(packageCurrencyLimit);
            });
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void updateAll(AgentPackage agentPackage, List<PackageCurrencyLimitDto> packageCurrencyLimits) {
        packageCurrencyLimitRepository.deleteByAgentPackage(agentPackage);
        if(CollectionUtils.isNotEmpty(packageCurrencyLimits)){
            packageCurrencyLimits.forEach(packageCurrencyLimitDto  -> {
                Currency currency = currencyService.findByName(packageCurrencyLimitDto.getCurrencyName());
                PackageCurrencyLimit packageCurrencyLimit = new PackageCurrencyLimit();
                packageCurrencyLimit.setDailyTransactionCount(packageCurrencyLimitDto.getDailyTransactionCount());
                packageCurrencyLimit.setWeeklyTransactionCount(packageCurrencyLimitDto.getWeeklyTransactionCount());
                packageCurrencyLimit.setMonthlyTransactionCount(packageCurrencyLimitDto.getMonthlyTransactionCount());
                packageCurrencyLimit.setDailyTransactionAmount(packageCurrencyLimitDto.getDailyTransactionAmount());
                packageCurrencyLimit.setWeeklyTransactionAmount(packageCurrencyLimitDto.getWeeklyTransactionAmount());
                packageCurrencyLimit.setMonthlyTransactionAmount(packageCurrencyLimitDto.getMonthlyTransactionAmount());

                packageCurrencyLimit.setDailySendingLimit(packageCurrencyLimitDto.getDailySendingLimit());
                packageCurrencyLimit.setDailyReceivingLimit(packageCurrencyLimitDto.getDailyReceivingLimit());
                packageCurrencyLimit.setWeeklySendingLimit(packageCurrencyLimitDto.getWeeklyReceivingLimit());
                packageCurrencyLimit.setWeeklyReceivingLimit(packageCurrencyLimitDto.getWeeklyReceivingLimit());
                packageCurrencyLimit.setMonthlySendingLimit(packageCurrencyLimitDto.getMonthlySendingLimit());
                packageCurrencyLimit.setMonthlyReceivingLimit(packageCurrencyLimitDto.getMonthlyReceivingLimit());

                packageCurrencyLimit.setCreateDate(Instant.now());
                packageCurrencyLimit.setCurrency(currency);
                packageCurrencyLimit.setAgentPackage(agentPackage);
                packageCurrencyLimitRepository.save(packageCurrencyLimit);
            });
        }
    }
}
