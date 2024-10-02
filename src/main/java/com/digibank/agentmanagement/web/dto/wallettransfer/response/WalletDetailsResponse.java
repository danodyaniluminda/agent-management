package com.digibank.agentmanagement.web.dto.wallettransfer.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletDetailsResponse {

    private String walletId;
    private String walletUserId;
    private String walletUserType;
    private String currencyCode;
    private String countryCode;
    private String balance;
}
