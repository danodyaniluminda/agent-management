package com.digibank.agentmanagement.web.dto.commission;


import com.digibank.agentmanagement.domain.*;
import com.digibank.agentmanagement.domain.packagemanagement.CommissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentCommissionDTO {
    private double commissionAmount;
    private double commissionFixedAmount;
    private double commissionPercentage;
    private CommissionType commissionType;
    private AgentType agentType;
    private String agentRegisteredBy;
    private String superAgentId;
    private String agentLinkedTo;
    private Long agentId;
}
