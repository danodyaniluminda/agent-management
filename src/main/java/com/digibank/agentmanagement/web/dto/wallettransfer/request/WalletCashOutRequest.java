package com.digibank.agentmanagement.web.dto.wallettransfer.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletCashOutRequest {

    @NonNull
    private String toAccountNumber;
    @NonNull
    private BigDecimal amount;
    @NonNull
    private String reason;
    @NonNull
    private String mfaToken;
    @NonNull
    private String userType;
    @NonNull
    private String userId;
    @NonNull
    private BigDecimal fee;
    @NonNull
    private String currencyName;
    @NonNull
    private String type;

    @NonNull
    private String bankCustomerId;

    private String sender;
    private String receiver;
}
