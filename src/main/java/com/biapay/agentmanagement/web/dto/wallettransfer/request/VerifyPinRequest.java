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
public class VerifyPinRequest {


    private String phoneNumber;

    private String mfaCode;
}
