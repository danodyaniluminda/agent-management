package com.biapay.agentmanagement.web.dto.accounttransfer.request;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashWithDrawlRequest extends CashTransferRequest {

    private String sender;
    private String receiver;
}
