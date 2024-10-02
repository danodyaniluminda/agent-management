package com.digibank.agentmanagement.domain;

import com.digibank.agentmanagement.domain.packagemanagement.CommissionType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "agent_operation_commission")
public class AgentOperationCommission implements Serializable {

  private static final long serialVersionUID = 584861899224854004L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "agent_operation_id")
  private AgentOperation agentOperation;

  @Enumerated(EnumType.STRING)
  private CommissionType commissionType;

  private BigDecimal percentage;
  private BigDecimal fixedAmount;

  private Boolean active;

  private LocalDateTime insertDate;
  private Integer lowerBound;
  private Integer upperBound;
}
