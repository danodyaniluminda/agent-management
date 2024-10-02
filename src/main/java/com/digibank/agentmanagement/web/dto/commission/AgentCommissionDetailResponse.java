package com.digibank.agentmanagement.web.dto.commission;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AgentCommissionDetailResponse {

  private String currencyCode;
  private Double transactionFee;
  private Long feeId;
  private BigDecimal fixedAmount;
  private BigDecimal percentageAmount;
  private Long paymentMethodId;
  private Long subscriptionPlanId;
  private String commissionType;
  private Float commissionPercentage;
  private BigDecimal commissionAmount;
  private String supervisorWalletId;
  private String settlementPeriod;
  private BigDecimal finalAmount;
}
