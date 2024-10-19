package com.biapay.agentmanagement.web.dto.accounttransfer.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementRequest {

    @NotNull
    private String accountNumber;
    @NotNull
    private Boolean sendViaEmail;
    @NotNull
    private String mfaToken;
    @NotNull
    private String customerId;
    @NotNull
    private String fromDate;
    @NotNull
    private String toDate;
    @NotNull
    private String pageNumber;
    @NotNull
    private String size;
}
