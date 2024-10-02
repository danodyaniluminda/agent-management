package com.digibank.agentmanagement.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransferCommissionDto {

    private BigDecimal agentMemberCommission;
    private BigDecimal agentCommission;
    private BigDecimal agentBankerCommission;

    private String agentBankerAccountNumber;
    private String agentAccountNumber;
    private String agentMemberAccountNumber;
}
