package com.biapay.agentmanagement.web.dto.accounttransfer.response;

import lombok.Data;

@Data
public class AccountBillPayResponse {

    private String bankTxnId;
    private String status;
    private String externalStatus;
    private String receiptNumber;
}
