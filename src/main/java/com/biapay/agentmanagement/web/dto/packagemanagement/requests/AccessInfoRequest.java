package com.biapay.agentmanagement.web.dto.packagemanagement.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccessInfoRequest {

    private String userName;
    private String browser;
}
