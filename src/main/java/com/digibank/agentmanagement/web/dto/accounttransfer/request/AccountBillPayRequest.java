package com.digibank.agentmanagement.web.dto.accounttransfer.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountBillPayRequest {

    private String payItemId;
    private String mfaToken;
    private String bankAc;
    private String paymentCategory;
    private String serviceNumber;
    private String phoneNumber;
    private String email;
    private String customerId;
    private String type;
    private String currencyName;
    private BigDecimal amount;
    private BigDecimal fee;
}
