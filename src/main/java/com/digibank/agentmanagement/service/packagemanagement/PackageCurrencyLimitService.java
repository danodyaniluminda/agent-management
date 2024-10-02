package com.digibank.agentmanagement.service.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.AgentPackage;
import com.digibank.agentmanagement.domain.packagemanagement.Currency;
import com.digibank.agentmanagement.domain.packagemanagement.PackageCurrencyLimit;
import com.digibank.agentmanagement.web.dto.packagemanagement.PackageCurrencyLimitDto;

import java.util.List;

public interface PackageCurrencyLimitService {

    List<PackageCurrencyLimitDto> getAllByAgentPackage(AgentPackage agentPackage);

    void addAll(AgentPackage agentPackage, List<PackageCurrencyLimitDto> packageCurrencyLimits);

    void updateAll(AgentPackage agentPackage, List<PackageCurrencyLimitDto> packageCurrencyLimits);

    List<PackageCurrencyLimit> findAllByAgentPackage(AgentPackage agentPackage);

    PackageCurrencyLimit checkLimitsRegisteredForCurrencyInPackage(Currency currency, AgentPackage agentPackage);
}
