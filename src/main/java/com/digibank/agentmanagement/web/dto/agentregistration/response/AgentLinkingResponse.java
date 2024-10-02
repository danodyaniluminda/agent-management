package com.digibank.agentmanagement.web.dto.agentregistration.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentLinkingResponse {
    private String customerBankName;

    private String customerBankPhone;

    private String customerBankEmail;

}
