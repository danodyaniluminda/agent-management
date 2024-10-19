package com.biapay.agentmanagement.utils;

import com.biapay.agentmanagement.domain.packagemanagement.AgentLimitProfile;
import com.biapay.agentmanagement.domain.packagemanagement.PackageCurrencyLimit;
import com.biapay.agentmanagement.exception.TransactionLimitException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@AllArgsConstructor
@Slf4j
public class AgentLimitManagementUtils {

    public void checkReceivingTransactionLimit(LocalDate todayDate, BigDecimal currentTransactionAmount, PackageCurrencyLimit packageCurrencyLimit, AgentLimitProfile agentLimitProfile) throws TransactionLimitException {
        checkDailyReceivingLimit(todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        checkWeeklyReceivingLimit(todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        checkMonthlyReceivingLimit(todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
    }

    private void checkDailyReceivingLimit(LocalDate todayDate, BigDecimal currentTransactionAmount, PackageCurrencyLimit packageCurrencyLimit, AgentLimitProfile agentLimitProfile) {
        log.info("checking daily limits availability for date: {}, for amount: {}, with packageCurrencyLimit: {} and agentLimitProfile: {}", todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        if (todayDate.isEqual(agentLimitProfile.getDailyCycleDate())) {
            if (agentLimitProfile.getDailyReceivingLimit().compareTo(packageCurrencyLimit.getDailyReceivingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.DAILY_TRANSACTION_AMOUNT_LIMIT_REACHED);
            } else if (agentLimitProfile.getDailyReceivingLimit().add(currentTransactionAmount).compareTo(packageCurrencyLimit.getDailyReceivingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.DAILY_TRANSACTION_AMOUNT_ABOVE_LIMIT, packageCurrencyLimit.getDailyReceivingLimit().subtract(agentLimitProfile.getDailyReceivingLimit()).toString());
            }
        } else if (todayDate.isAfter(agentLimitProfile.getDailyCycleDate())) {
            agentLimitProfile.setDailyReceivingLimit(new BigDecimal("0"));
        } else {
            throw new TransactionLimitException(ApiError.PACKAGE_CURRENCY_NOT_AVAILABLE);
        }
    }

    private void checkWeeklyReceivingLimit(LocalDate todayDate, BigDecimal currentTransactionAmount, PackageCurrencyLimit packageCurrencyLimit, AgentLimitProfile agentLimitProfile) {
        log.info("checking weekly limits availability for date: {}, for amount: {}, with packageCurrencyLimit: {} and agentLimitProfile: {}", todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        if (todayDate.isEqual(agentLimitProfile.getWeeklyCycleDate()) || todayDate.isBefore(agentLimitProfile.getWeeklyCycleDate())) {
            if (agentLimitProfile.getWeeklyReceivingLimit().compareTo(packageCurrencyLimit.getWeeklyReceivingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.WEEKLY_TRANSACTION_AMOUNT_LIMIT_REACHED);
            } else if (agentLimitProfile.getWeeklyReceivingLimit().add(currentTransactionAmount)
                    .compareTo(packageCurrencyLimit.getWeeklyReceivingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.WEEKLY_TRANSACTION_AMOUNT_ABOVE_LIMIT,
                        packageCurrencyLimit.getWeeklyReceivingLimit().subtract(agentLimitProfile.getWeeklyReceivingLimit()).toString());
            }
        } else if (todayDate.isAfter(agentLimitProfile.getDailyCycleDate())) {
            // Week completed yesterday. need to reset date from current date plus 6 days and limits
            agentLimitProfile.setWeeklyReceivingLimit(new BigDecimal("0"));
        }
    }

    private void checkMonthlyReceivingLimit(LocalDate todayDate, BigDecimal currentTransactionAmount, PackageCurrencyLimit packageCurrencyLimit, AgentLimitProfile agentLimitProfile) {
        log.info("checking monthly limits availability for date: {}, for amount: {}, with packageCurrencyLimit: {} and agentLimitProfile: {}", todayDate, currentTransactionAmount, packageCurrencyLimit, agentLimitProfile);
        if (todayDate.isEqual(agentLimitProfile.getMonthlyCycleDate()) || todayDate.isBefore(agentLimitProfile.getMonthlyCycleDate())) {
            if (agentLimitProfile.getMonthlyReceivingLimit().compareTo(packageCurrencyLimit.getMonthlyReceivingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.MONTHLY_TRANSACTION_AMOUNT_LIMIT_REACHED);
            } else if (agentLimitProfile.getMonthlyReceivingLimit().add(currentTransactionAmount)
                    .compareTo(packageCurrencyLimit.getMonthlyReceivingLimit()) >= 0) {
                throw new TransactionLimitException(ApiError.MONTHLY_TRANSACTION_AMOUNT_ABOVE_LIMIT,
                        packageCurrencyLimit.getMonthlyReceivingLimit().subtract(agentLimitProfile.getMonthlyReceivingLimit()).toString());
            }
        } else if (todayDate.isAfter(agentLimitProfile.getDailyCycleDate())) {
            // Month completed yesterday. need to reset date and limits
            agentLimitProfile.setMonthlyCycleDate(todayDate.plusDays(29));
            agentLimitProfile.setMonthlyReceivingLimit(new BigDecimal("0"));
        }
    }

    public void updateTransactionLimit(AgentLimitProfile agentLimitProfile, BigDecimal currentTransactionAmount) throws TransactionLimitException {
        log.info("Update agentLimitProfile with amount: {} and plus a transaction count of all limits", currentTransactionAmount);
        agentLimitProfile.setDailyTransactionCount(agentLimitProfile.getDailyTransactionCount() + 1);
        agentLimitProfile.setWeeklyTransactionCount(agentLimitProfile.getWeeklyTransactionCount() + 1);
        agentLimitProfile.setMonthlyTransactionCount(agentLimitProfile.getMonthlyTransactionCount() + 1);

        agentLimitProfile.setDailyTransactionAmount(agentLimitProfile.getDailyTransactionAmount().add(currentTransactionAmount));
        agentLimitProfile.setWeeklyTransactionAmount(agentLimitProfile.getWeeklyTransactionAmount().add(currentTransactionAmount));
        agentLimitProfile.setMonthlyTransactionAmount(agentLimitProfile.getMonthlyTransactionAmount().add(currentTransactionAmount));

        agentLimitProfile.setDailySendingLimit(agentLimitProfile.getDailySendingLimit().add(currentTransactionAmount));
        agentLimitProfile.setWeeklySendingLimit(agentLimitProfile.getWeeklySendingLimit().add(currentTransactionAmount));
        agentLimitProfile.setMonthlySendingLimit(agentLimitProfile.getMonthlySendingLimit().add(currentTransactionAmount));
    }

}
