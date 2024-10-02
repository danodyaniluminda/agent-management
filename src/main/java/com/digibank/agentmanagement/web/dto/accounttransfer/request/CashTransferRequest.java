package com.digibank.agentmanagement.web.dto.accounttransfer.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashTransferRequest {

    @NonNull
    private BigDecimal amount;
    @NonNull
    private String description;
    @NonNull
    private String mfaToken;
    @NonNull
    private String type;
    @NonNull
    private String currencyName;
    @NonNull
    private Integer feeId;
    @NonNull
    private BigDecimal fee;
    @NonNull
    private String senderCustomerId;
    @NonNull
    private String receiverName;
    @NonNull
    private Boolean saveAsBeneficiary;
    @NonNull
    private String customerBankAccount;

}
