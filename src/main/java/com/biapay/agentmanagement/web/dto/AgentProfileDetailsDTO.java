package com.biapay.agentmanagement.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentProfileDetailsDTO {
    private Long agentProfileDetailsId;

    private String totalDebitLimit ;

    private String totalCreditLimit ;

    private String cashInLimit ;

    private String cashOutLimit ;

    private String fundTransferLimit ;

    private String remittanceLimit ;

    private String billAirtimeLimit ;

    private String lTInLimit ;

    private String lTOutLimit ;

    private String floatLimit ;
}
