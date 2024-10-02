package com.digibank.agentmanagement.web.dto.response;


import com.digibank.agentmanagement.domain.AgentStatus;
import com.digibank.agentmanagement.domain.AgentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentDetailsResponse {

    private Long agentId;

    private String iamId;

    private String firstName;

    private String lastName;
    
    private String agentName ;

    private String phoneNo ;

    private String agentEmailAddress ;

    private AgentType agentType;
    
    private AgentStatus status;
    
    private LocalDateTime registrationDateTime;

}
