package com.digibank.agentmanagement.web.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountToAccountFTResponse {

    private String eventNo ;
    private String eventDate ;
    private String amount ;
    private String fees ;
    private String status ;
    private String branchCode ;
    private String accountNo ;
    private String reason ;
    private String opeCode ;
    private String opeTitle ;
    private String custId ;
    private String custName ;
    private String recipientName ;
    private String recipientAccount ;
    private String trxId ;
    private String eventLikeModel ;
    private String eventId ;
    private String trxAuthoriseCode ;
    private String secretCode ;
    private String secretCodeValidity ;
    private String senderPhoneNo ;
    private String recipientPhoneNo ;
    private String referenceExterne ;
    private String referenceExterne2 ;
    private String externalStatus ;

}

