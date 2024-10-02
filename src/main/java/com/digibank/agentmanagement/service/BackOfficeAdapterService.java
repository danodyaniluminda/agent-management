package com.digibank.agentmanagement.service;

import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.utils.AgentManagementConstants;
import com.digibank.agentmanagement.utils.AgentManagementPropertyHolder;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class BackOfficeAdapterService {
    private final AgentManagementPropertyHolder agentManagementPropertyHolder;
    private final AgentManagementUtils agentManagementUtils;

    public void AgentRegistrationBackOfficeCall(AgentDetails agentDetails) {


        HttpResponse<String> response =
        Unirest.post(agentManagementPropertyHolder.getBackOfficeAdaptorServiceURL() + "public/agent/registration")
                .header("Content-Type", "application/json")
                //.header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                .body(AgentRegistrationBackOfficeCall.builder()
                        .name(agentDetails.getFirstName())
                        .username(agentDetails.getFullName())
                        .email(agentDetails.getAgentEmailAddress())
                        .phoneNo(agentDetails.getPhoneNo())
                        .iamId(agentDetails.getIamId())
                        .build()
                ).asString();

        log.info("Response: {}", response.getBody());

    }




    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class AgentRegistrationBackOfficeCall {
        private String name;
        private String username;
        private String email;
        private String phoneNo;
        private String iamId;
    }
}
