package com.biapay.agentmanagement.web.dto.packagemanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AssetDto {

    private Long assetId;
    private String name;
    private Boolean active;
    private Instant createDate;

    private List<OperationDto> operations;
}
