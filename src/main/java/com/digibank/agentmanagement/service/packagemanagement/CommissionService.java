package com.digibank.agentmanagement.service.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Commission;
import com.digibank.agentmanagement.domain.packagemanagement.CommissionType;
import com.digibank.agentmanagement.web.dto.commission.AgentCommissionDTO;
import com.digibank.agentmanagement.web.dto.packagemanagement.CommissionDto;

import java.math.BigDecimal;
import java.util.List;

public interface CommissionService {

    Commission find(Long commissionId);

    Commission find(String commissionId);

    List<CommissionDto> getAll();

    CommissionDto get(Long commissionId);

    CommissionDto add(CommissionDto commissionRequest);

    CommissionDto update(Long commissionId, CommissionDto commissionRequest);

    void delete(Long commissionId);

    void delete(String commissionId);

    Commission findByCommissionType(CommissionType commissionType);

    AgentCommissionDTO getCommissionByAgent(String agentMobileNumber, String operationName);

    BigDecimal calculateCommissionByAgent(String agentMobileNumber, String operationName, BigDecimal amount);
}
