package com.biapay.agentmanagement.web.dto.accounttransfer.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceInquiryRequest {

    @NotNull
    private String accountNumber;
    @NotNull
    private String sendType;
    @NotNull
    private String mfaToken;
    @NotNull
    private String customerId;
}
