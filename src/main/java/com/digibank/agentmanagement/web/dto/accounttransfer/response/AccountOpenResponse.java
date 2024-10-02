package com.digibank.agentmanagement.web.dto.accounttransfer.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
public class AccountOpenResponse {

    private String code;
    private String description;
    private String details;
}
