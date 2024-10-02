package com.digibank.agentmanagement.web.dto.response;


import com.digibank.agentmanagement.domain.MFAChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GenericTransactionRequest {

    private String AccountNumber ;

    private String sender ;

    private  String receiver ;

    private String amount ;

    private String description ;

    private String mfaToken ;

    private String type ;

    private String fromDate ;

    private String toDate ;

    private String pageNumber ;

    private String size ;
    
    private String customerId;
    
    private String toAccountNumber ;
    
    private String reason;
    
    private String beneficiaryName;
    
    private String beneficiaryWalletId;
    
    private String currencyCode;

    private String customerMobile;

    private String customerType;

    private MFAChannel mfaChannel;
}
