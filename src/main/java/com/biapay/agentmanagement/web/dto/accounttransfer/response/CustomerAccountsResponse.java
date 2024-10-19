package com.biapay.agentmanagement.web.dto.accounttransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CustomerAccountsResponse {

    private String accNo;
    private BigDecimal balance;
    private BigDecimal openingBalance;
    private String gcreateDate;
    private String owner;
    private String accType;
    private String currency;
    private String countryCode;
    BranchResponse branch;
}
