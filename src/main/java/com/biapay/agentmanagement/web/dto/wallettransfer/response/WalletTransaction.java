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
public class WalletTransaction {

    private int walletTransactionId;
    private Wallet fromWallet;
    private Wallet toWallet;
    private BigDecimal amount;
    private BigDecimal fee;
    private String walletTransactionType;
    private String transactionReference;
    private String transactionDescription;
    private String remarks;
    private String createdDate;
    private String fromWalletUserName;
    private String toWalletUserName;
    private String fromWalletMobileNumber;
    private String toWalletMobileNumber;
}
