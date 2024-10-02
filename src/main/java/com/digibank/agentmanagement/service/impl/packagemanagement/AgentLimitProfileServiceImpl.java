package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.AgentStatus;
import com.digibank.agentmanagement.domain.packagemanagement.AgentLimitProfile;
import com.digibank.agentmanagement.domain.packagemanagement.Currency;
import com.digibank.agentmanagement.domain.packagemanagement.PackageCurrencyLimit;
import com.digibank.agentmanagement.exception.AccountNotActiveException;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.repository.packagemanagement.AgentLimitProfileRepository;
import com.digibank.agentmanagement.service.AgentDetailsService;
import com.digibank.agentmanagement.service.packagemanagement.AgentLimitProfileService;
import com.digibank.agentmanagement.service.packagemanagement.CurrencyService;
import com.digibank.agentmanagement.service.packagemanagement.PackageCurrencyLimitService;
import com.digibank.agentmanagement.utils.AgentLimitManagementUtils;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.dto.accounttransfer.request.AgentLimitValidationRequest;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.AgentLimitValidationResponse;
import lombok.AllArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AgentLimitProfileServiceImpl implements AgentLimitProfileService {

    @Autowired
    private AgentLimitProfileRepository agentLimitProfileRepository;
    @Autowired
    private AgentDetailsService agentDetailsService;
    //    @Lazy
    @Autowired
    private AgentLimitManagementUtils agentLimitManagementUtils;
    @Autowired
    private PackageCurrencyLimitService packageCurrencyLimitService;
    @Autowired
    private CurrencyService currencyService;

    @Override
    public AgentLimitProfile getAgentLimitProfileByAgentIdAndCurrencyName(Long agentId, String currencyName) {
        return agentLimitProfileRepository.findByAgentIdAndCurrencyName(agentId, currencyName)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));

    }

    @Override
    public AgentLimitProfile getAgentLimitProfileByAgentId(Long agentId) {
        return agentLimitProfileRepository.findByAgentId(agentId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));

    }

    @Override
    @Transactional
    public AgentLimitProfile save(AgentLimitProfile agentLimitProfile) {
        return agentLimitProfileRepository.save(agentLimitProfile);
    }

    @Override
    public AgentLimitValidationResponse validateAgentLimit(String uuid, KeycloakAuthenticationToken token, AgentLimitValidationRequest validationRequest) {
        AgentLimitValidationResponse validationResponse = new AgentLimitValidationResponse();
        validationResponse.setAllowed(false);
        validationResponse.setAgentId(validationRequest.getReceivingAgentId());
        validationResponse.setAmount(validationRequest.getAmount());

        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
//        agentManagementUtils.checkAgentValidity(agentDetails);

        if (agentDetails.getStatus() != AgentStatus.ACTIVE) {
            throw new AccountNotActiveException(ApiError.AGENT_NOT_ACTIVE);
        }

        Optional<AgentLimitProfile> agentLimitProfile = agentLimitProfileRepository.findByAgentId(agentDetails.getAgentId());
        if (agentLimitProfile.isPresent()) {
            Currency currency = currencyService.findByName(agentLimitProfile.get().getCurrencyName());
            PackageCurrencyLimit packageCurrencyLimit = packageCurrencyLimitService.checkLimitsRegisteredForCurrencyInPackage(currency, agentDetails.getAgentPackage());

            //get agent available limit
            LocalDateTime todayDateTime = LocalDateTime.now();
            agentLimitManagementUtils.checkReceivingTransactionLimit(todayDateTime.toLocalDate(), validationRequest.getAmount(), packageCurrencyLimit, agentLimitProfile.get());

            validationResponse.setAllowed(true);
        }

        return validationResponse;
    }
}
