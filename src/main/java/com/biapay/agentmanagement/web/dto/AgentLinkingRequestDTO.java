package com.biapay.agentmanagement.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentLinkingRequestDTO {

    private String agentEmailAddress;

    private String firstName;

    private String lastName;

    private String phoneNo;

    private String iamId;


}
