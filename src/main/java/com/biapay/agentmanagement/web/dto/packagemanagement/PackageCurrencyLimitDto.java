package com.biapay.agentmanagement.web.dto.packagemanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PackageCurrencyLimitDto {

    private Long limitId;
    private int dailyTransactionCount;
    private int weeklyTransactionCount;
    private int monthlyTransactionCount;
    private BigDecimal dailyTransactionAmount;
    private BigDecimal weeklyTransactionAmount;
    private BigDecimal monthlyTransactionAmount;
    private String currencyName;

    //used only in response
    private Instant createDate;
    private CurrencyDto currency;

    //
    private BigDecimal dailySendingLimit;
    private BigDecimal weeklySendingLimit;
    private BigDecimal monthlySendingLimit;
    private BigDecimal dailyReceivingLimit;
    private BigDecimal weeklyReceivingLimit;
    private BigDecimal monthlyReceivingLimit;
}
