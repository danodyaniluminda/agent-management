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
public class ResetPasswordRequest {

    @NotNull
    private String phoneNumber;
    @NotNull
    private String password;
    @NotNull
    private String confirmPassword;
}
