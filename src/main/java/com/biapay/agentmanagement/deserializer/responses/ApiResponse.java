package com.biapay.agentmanagement.deserializer.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApiResponse {

    private String errorCode;
    private String type;
    private String title;
    private int status;
    private String detail;
}
