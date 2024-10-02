package com.digibank.agentmanagement.service;

import com.digibank.agentmanagement.deserializer.responses.ApiResponse;
import com.digibank.agentmanagement.exception.ServiceException;
import com.digibank.agentmanagement.utils.AgentManagementConstants;
import com.digibank.agentmanagement.utils.AgentManagementPropertyHolder;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.CustomerAccountsResponse;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.CustomerDetailsResponse;
import com.digibank.agentmanagement.web.dto.usermanagement.response.WalletCustomerDetails;
import com.google.common.reflect.TypeToken;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserManagementAdapterService {

    private final AgentManagementPropertyHolder agentManagementPropertyHolder;
    private final AgentManagementUtils agentManagementUtils;

    public WalletCustomerDetails getCustomerDetailsByPhone(KeycloakAuthenticationToken token, String phoneNumber) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getUserManagementURL() + "/api-internal/customers/mobileNumber/{phoneNumber}")
                        .routeParam("phoneNumber", phoneNumber)
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<WalletCustomerDetails>(){}.getType());
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }
}
