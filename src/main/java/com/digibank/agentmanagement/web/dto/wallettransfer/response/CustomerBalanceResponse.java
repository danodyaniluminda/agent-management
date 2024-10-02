package com.digibank.agentmanagement.web.dto.wallettransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CustomerBalanceResponse {

    private String walletUserId;
    private String walletUserType;
    private String countryCode;
    private String currencyCode;
    private BigDecimal balance;
    private String walletStatus;
}
