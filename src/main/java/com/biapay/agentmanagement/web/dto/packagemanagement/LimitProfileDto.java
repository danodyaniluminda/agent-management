package com.biapay.agentmanagement.web.dto.packagemanagement;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LimitProfileDto {

    private String currency;

    private BigDecimal totalDailyLimit;
    private int totalDailyTransactionCount;
    private BigDecimal availableDailyLimit;
    private int availableDailyTransactionCount;

    private BigDecimal totalDailySendingLimit;
    private BigDecimal totalDailyReceivingLimit;
    private BigDecimal availableDailySendingLimit;
    private BigDecimal availableDailyReceivingLimit;

    private BigDecimal totalWeeklyLimit;
    private int totalWeeklyTransactionCount;
    private BigDecimal availableWeeklyLimit;
    private int availableWeeklyTransactionCount;
    private String weeklyCycleDate;

    private BigDecimal totalWeeklySendingLimit;
    private BigDecimal totalWeeklyReceivingLimit;
    private BigDecimal availableWeeklySendingLimit;
    private BigDecimal availableWeeklyReceivingLimit;

    private BigDecimal totalMonthlyLimit;
    private int totalMonthlyTransactionCount;
    private BigDecimal availableMonthlyLimit;
    private int availableMonthlyTransactionCount;

    private BigDecimal totalMonthlySendingLimit;
    private BigDecimal availableMonthlySendingLimit;
    private BigDecimal totalMonthlyReceivingLimit;
    private BigDecimal availableMonthlyReceivingLimit;

    private String monthlyCycleDate;


}
