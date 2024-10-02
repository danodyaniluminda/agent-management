package com.digibank.agentmanagement.domain.packagemanagement;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "PackageCurrencyLimit")
public class PackageCurrencyLimit implements Serializable {

    private static final long serialVersionUID = 584861899224854006L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "packageCurrencyLimits_limit_id_seq")
    @SequenceGenerator(name = "packageCurrencyLimits_limit_id_seq", allocationSize = 1, sequenceName = "packageCurrencyLimits_limit_id_seq")
    private Long limitId;

    private int dailyTransactionCount;

    private int weeklyTransactionCount;

    private int monthlyTransactionCount;

    private BigDecimal dailyTransactionAmount;

    private BigDecimal weeklyTransactionAmount;

    private BigDecimal monthlyTransactionAmount;

    private BigDecimal dailySendingLimit;

    private BigDecimal weeklySendingLimit;

    private BigDecimal monthlySendingLimit;

    private BigDecimal dailyReceivingLimit;

    private BigDecimal weeklyReceivingLimit;

    private BigDecimal monthlyReceivingLimit;

    private Instant createDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currencyId")
    private Currency currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "packageId")
    private AgentPackage agentPackage;
}
