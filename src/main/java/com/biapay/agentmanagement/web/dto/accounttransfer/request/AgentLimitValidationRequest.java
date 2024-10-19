package com.biapay.agentmanagement.web.dto.accounttransfer.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentLimitValidationRequest extends CashTransferRequest {

    private Long receivingAgentId;
    private BigDecimal amount;
}
