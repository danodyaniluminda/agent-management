package com.biapay.agentmanagement.web.dto.accounttransfer.request;

import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashDepositRequest extends CashTransferRequest {

    private String receiver;
    private String sender;
}
