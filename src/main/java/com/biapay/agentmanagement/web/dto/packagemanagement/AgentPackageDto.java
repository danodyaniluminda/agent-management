package com.biapay.agentmanagement.web.dto.packagemanagement;

import com.biapay.agentmanagement.domain.AgentType;
import com.biapay.agentmanagement.domain.packagemanagement.*;
import com.biapay.agentmanagement.web.dto.packagemanagement.requests.AssetOperationPermissionDto;
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
public class AgentPackageDto {


    private Long packageId;
    private String name;
    private PackageType packageType;
    private AgentType agentType;
    private Boolean isDefault;
    private Boolean isFeatured;
    private String channel;
    private SettlementPeriod settlementPeriod;
    private String planPrice;
    private Boolean active;
    private Instant createDate;
    private List<PackageCurrencyLimitDto> currencyLimitProfiles;
    private List<AssetOperationPermissionDto> operationPermissionProfiles;
//    private List<PackageOperationPermissionDto> packageOperationPermission;
}
