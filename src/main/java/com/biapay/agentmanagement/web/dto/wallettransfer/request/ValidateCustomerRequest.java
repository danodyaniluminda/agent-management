package com.biapay.agentmanagement.web.dto.wallettransfer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ValidateCustomerRequest {

    @NotNull
    private String phoneNumber;
    @NotNull
    private String idDocumentNumber;
    @NotNull
    private String idDocumentType;

    private String bankCustomerId;
}
