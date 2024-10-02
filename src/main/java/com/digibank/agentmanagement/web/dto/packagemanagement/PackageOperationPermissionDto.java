package com.digibank.agentmanagement.web.dto.packagemanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PackageOperationPermissionDto {

    private Long permissionId;
    private String Channel;
    private Instant effectiveFrom;
    private Instant effectiveTo;
    private boolean active;
    private Instant createDate;
    private Long assetId;
    private String assetName;

    private Long operationId;
    private String operationName;

}
