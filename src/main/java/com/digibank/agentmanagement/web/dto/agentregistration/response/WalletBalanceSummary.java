package com.digibank.agentmanagement.web.dto.agentregistration.response;


import com.digibank.agentmanagement.web.dto.wallettransfer.response.CustomerBalanceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletBalanceSummary {


    private BigDecimal liquidityBalance;
    private CustomerBalanceResponse walletBalance;
}
