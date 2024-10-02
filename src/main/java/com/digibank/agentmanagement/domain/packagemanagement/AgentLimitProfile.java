package com.digibank.agentmanagement.domain.packagemanagement;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "AgentLimitProfile")
@Getter
@Setter
@ToString
public class AgentLimitProfile implements Serializable {

    private static final long serialVersionUID = 584861899224854012L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agentLimitProfiles_limitProfile_id_seq")
//    @SequenceGenerator(
//            name = "agentLimitProfiles_limitProfile_id_seq",
//            allocationSize = 1,
//            sequenceName = "agentLimitProfiles_limitProfile_id_seq")
    private Long limitProfileId;

    private String channel;

    private int dailyTransactionCount;

    private int weeklyTransactionCount;

    private int monthlyTransactionCount;

    private BigDecimal dailyTransactionAmount;

    private BigDecimal weeklyTransactionAmount;

    private BigDecimal monthlyTransactionAmount;

    private LocalDate dailyCycleDate;

    private LocalDate weeklyCycleDate;

    private LocalDate monthlyCycleDate;

    private Instant createDate;

    private String currencyName;

    private Long agentId;

    //
    private BigDecimal dailySendingLimit;
    private BigDecimal weeklySendingLimit;
    private BigDecimal monthlySendingLimit;
    private BigDecimal dailyReceivingLimit;
    private BigDecimal weeklyReceivingLimit;
    private BigDecimal monthlyReceivingLimit;
}
