package com.digibank.agentmanagement.web.dto.packagemanagement.requests;

import com.digibank.agentmanagement.web.dto.packagemanagement.OperationPermissionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AssetOperationPermissionDto {

    private String assetName;
    private List<OperationPermissionDto> operationPermissions;
}
