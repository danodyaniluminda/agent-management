package com.digibank.agentmanagement.web.dto.wallettransfer.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SendMoneyToNonWalletRequest {

    @NonNull
    private String debtorUserType;
    @NonNull
    private String debtorUserId;
    @NonNull
    private String creditorUserType;
    @NonNull
    private String creditorUserId;
    @NonNull
    private BigDecimal amount;
    @NonNull
    private String reason;
    @NonNull
    private String mfaToken;
    @NonNull
    private BigDecimal fee;
    @NonNull
    private String currencyName;
    @NonNull
    private String type;
}
