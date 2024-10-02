package com.digibank.agentmanagement.web.dto.packagemanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OperationDto {

    private Long operationId;
    private String name;
    private Boolean active;
    private Instant createDate;
    private String assetName;

    //use in response
    private AssetDto asset;
}
