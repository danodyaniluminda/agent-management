package com.digibank.agentmanagement.web.dto.wallettransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletToWalletResponse {

    private String walletTransactionId;

    private WalletDetailsResponse fromWallet;

    private WalletDetailsResponse toWallet;

    private String amount;

    private String fee;
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
