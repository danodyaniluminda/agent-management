package com.biapay.agentmanagement.web.dto.wallettransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Wallet {

    private int walletId;
    private String walletUserId;
    private String walletUserType;
    private String currencyCode;
    private String countryCode;
    private BigDecimal balance;
    private String walletStatus;
}
