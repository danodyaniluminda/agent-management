package com.digibank.agentmanagement.web.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletAccountOpeningResponse {
    private String wallet_id ;
    private String name ;
    private String email ;
}
