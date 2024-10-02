package com.digibank.agentmanagement.web.dto.accounttransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BranchResponse {

    private String branchCode;
    private String name;
    private String adr;
    private String latitude;
    private String longitude;
    private String town;
}
