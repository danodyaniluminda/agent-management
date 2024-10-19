package com.biapay.agentmanagement.web.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GenericReceiptDataResponse {

    private  String accountNumber ;

    private String senderAccount ;

    private  String receiverAccount ;

    private String transactionAmount ;

    private String amountTransactionFee ;

    private String transactionStatus ;

    private String reason ;

    private String senderName ;

    private String receiverName ;

    private String transactionId;

    private String balance ;


}
