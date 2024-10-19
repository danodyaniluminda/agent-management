package com.biapay.agentmanagement.web.dto.accounttransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountStatementResponse {
    private String accNo;
    private String tranDate;
    private String amt;
    private String title;
    private String ope;
    private String sens;
}
