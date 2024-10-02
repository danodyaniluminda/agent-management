package com.digibank.agentmanagement.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentManagementResponse {

    private String code;
    private String message;
    private String details;
    private int developerCode;
    private String developerMessage;
    private Object data;
    private Throwable cause;
}
