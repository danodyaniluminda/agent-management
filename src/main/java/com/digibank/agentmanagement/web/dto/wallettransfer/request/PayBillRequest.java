package com.digibank.agentmanagement.web.dto.wallettransfer.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayBillRequest {

    private String payItemId;
    private BigDecimal amount;
    private String mfaToken;
    private BigDecimal fee;
    private String notifyEmail;
    private String type;
    private String currencyName;
}