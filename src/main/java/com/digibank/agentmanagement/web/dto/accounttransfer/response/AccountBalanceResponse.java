package com.digibank.agentmanagement.web.dto.accounttransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountBalanceResponse {

    private String accountNumber;
    private BigDecimal balance;
}
