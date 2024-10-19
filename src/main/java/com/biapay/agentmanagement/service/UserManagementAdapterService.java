package com.biapay.agentmanagement.service;

import com.biapay.agentmanagement.deserializer.responses.ApiResponse;
import com.biapay.agentmanagement.exception.ServiceException;
import com.biapay.agentmanagement.utils.AgentManagementConstants;
import com.biapay.agentmanagement.utils.AgentManagementPropertyHolder;
import com.biapay.agentmanagement.utils.AgentManagementUtils;
import com.biapay.agentmanagement.web.dto.usermanagement.response.WalletCustomerDetails;
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
