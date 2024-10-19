package com.biapay.agentmanagement.web.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GenericRejectionResponse {

    private String code ;

    private String message ;

    private String cause;

}
