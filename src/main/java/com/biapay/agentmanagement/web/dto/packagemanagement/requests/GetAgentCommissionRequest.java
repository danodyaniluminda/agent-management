package com.biapay.agentmanagement.web.dto.packagemanagement.requests;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetAgentCommissionRequest {

    private String agentMobileNumber;
    private String operation;
}
