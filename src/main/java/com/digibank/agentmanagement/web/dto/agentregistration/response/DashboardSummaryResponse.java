package com.digibank.agentmanagement.web.dto.agentregistration.response;


import com.digibank.agentmanagement.web.dto.packagemanagement.AgentPackageDto;
import com.digibank.agentmanagement.web.dto.packagemanagement.LimitProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DashboardSummaryResponse {

    private AgentPackageDto agentPackageDto;
    private List<WalletBalanceSummary> walletBalanceSummaryList;
    private List<LimitProfileDto> agentLimitProfiles;
    private long subAgentsCount;

}
