package com.digibank.agentmanagement.web.dto.packagemanagement.requests;

import com.digibank.agentmanagement.domain.packagemanagement.Asset;
import com.digibank.agentmanagement.enums.UserStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetAgentCommissionRequest {

    private String agentMobileNumber;
    private String operation;
}
