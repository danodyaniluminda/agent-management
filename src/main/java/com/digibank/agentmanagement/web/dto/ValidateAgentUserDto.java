package com.digibank.agentmanagement.web.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder(toBuilder = true)
public class ValidateAgentUserDto {
    @NonNull
    String mfaToken;

    @NonNull
    String userEmail;

    @NonNull
    String userPassword;
}
