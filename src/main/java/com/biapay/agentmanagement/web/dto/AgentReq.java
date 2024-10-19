package com.biapay.agentmanagement.web.dto;


import com.biapay.agentmanagement.domain.AgentLinkingStatus;
import com.biapay.agentmanagement.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentReq {

    private String transmissionDateTime;

    private String channelId;

    private String email;

    private String phoneNo;

    private String iamId;

    private String password;

    private String agentUId;

    private String mfaTokenType;

    private NotificationType notificationType;

    private String mfaToken;

    private String superAgentEmail;

    private String bankAccountNumber;

    private String bankCustomerId;

    private String childAgentEmail;

    private String coreBankPhone;

    private String coreBankEmail;

    private AgentLinkingStatus linkingStatus;


}
