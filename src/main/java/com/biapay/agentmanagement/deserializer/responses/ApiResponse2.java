package com.biapay.agentmanagement.deserializer.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApiResponse2 {

    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
