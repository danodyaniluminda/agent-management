package com.digibank.agentmanagement.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentManagementErrorResponse {

    private String type;
    private String title;
    private int status;
    private String detail;
    private String errorCode;
}
