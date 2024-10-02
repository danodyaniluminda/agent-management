package com.digibank.agentmanagement.web.dto.commission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AgentCommissionRequest {
  private Long paymentMethodId;
  private Long walletUserId;
  private String currencyCode;
  private Double transactionAmount;
  private Double fees;
  private String fromAccountType;
  private String toAccountType;
  private String walletTransactionType;
  private String paymentCategory;
}
