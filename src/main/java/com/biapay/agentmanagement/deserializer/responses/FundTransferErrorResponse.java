package com.biapay.agentmanagement.deserializer.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class FundTransferErrorResponse {

    private String code;
    private String cause;
    private String message;
}
