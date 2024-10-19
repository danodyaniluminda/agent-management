package com.biapay.agentmanagement.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegistrationSetPasswordReq {

    @NotNull private String phoneNumber;

    @NotNull private String password;

    @NotNull private String confirmPassword;

    @NotNull private String mfaCode;
}
