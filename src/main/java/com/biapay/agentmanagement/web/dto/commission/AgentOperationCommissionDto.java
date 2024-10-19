package com.biapay.agentmanagement.web.dto.commission;

import com.biapay.agentmanagement.domain.packagemanagement.CommissionType;
import com.biapay.agentmanagement.web.dto.AgentOperationDto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentOperationCommissionDto implements Serializable {

  private static final long serialVersionUID = 584861899224854004L;

  private Long id;

  private AgentOperationDto agentOperation;
  private Long agentOperationId;

  @Enumerated(EnumType.STRING)
  private CommissionType commissionType;

  private BigDecimal percentage;
  private BigDecimal fixedAmount;

  private Boolean active;

  private LocalDateTime insertDate;
  private BigDecimal transactionAmount;
}
