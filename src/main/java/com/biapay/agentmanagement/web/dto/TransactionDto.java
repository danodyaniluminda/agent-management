package com.biapay.agentmanagement.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransactionDto {
    // transactionKey
    private String transactionID;

    private LocalDate transactionDate;

    private String userName;

    private String userId;

    private String userType;

    private String operation;

    private String amount;

    private String fee;

    private String totalAmount;

    private String currency;

    private String senderKey;

    private String senderAccount;

    private String receiverKey;

    private String receiverAccount;

    private String reason ;

    private String transactionStatus;


}
