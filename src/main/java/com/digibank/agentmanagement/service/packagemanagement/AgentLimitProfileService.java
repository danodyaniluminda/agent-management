package com.digibank.agentmanagement.service.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.AgentLimitProfile;
import com.digibank.agentmanagement.web.dto.accounttransfer.request.AgentLimitValidationRequest;
import com.digibank.agentmanagement.web.dto.accounttransfer.request.CashWithDrawlRequest;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.AgentLimitValidationResponse;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.CashDepositWithdrawResponse;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

public interface AgentLimitProfileService {

    AgentLimitProfile getAgentLimitProfileByAgentIdAndCurrencyName(Long agentId, String currencyName);

    AgentLimitProfile getAgentLimitProfileByAgentId(Long agentId);

    AgentLimitProfile save(AgentLimitProfile agentLimitProfile);

    AgentLimitValidationResponse validateAgentLimit(String uuid, KeycloakAuthenticationToken token, AgentLimitValidationRequest cashWithDrawlRequest);
}
