package com.biapay.agentmanagement.web.dto.wallettransfer.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletBalanceRequest {
    @NonNull
    private String mobileNumber;
    @NonNull
    private String sendType;
    @NonNull
    private String mfaToken;

}
