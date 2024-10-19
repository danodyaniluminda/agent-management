package com.biapay.agentmanagement.web.dto.packagemanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountTransactionRequest {

//    {
//        "sender": "10005-00001-06128681051-36",
//            "receiver": "10005-00001-74900090004-01",
//            "amount": 120,
//            "description": "Test Bam",
//            "mfaToken": "694463",
//            "type": "FT"
//    }
    private String sender;
    private String receiver;
    private BigDecimal amount;
    private String description;
    private String mfaToken;
    private String type;
    private String currencyName;
}
