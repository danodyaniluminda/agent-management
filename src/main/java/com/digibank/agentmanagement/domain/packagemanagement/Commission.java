package com.digibank.agentmanagement.domain.packagemanagement;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Table(name = "Commission")
public class Commission implements Serializable {

    private static final long serialVersionUID = 584861899224854002L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commissions_commission_id_seq")
    @SequenceGenerator(name = "commissions_commission_id_seq", allocationSize = 1, sequenceName = "commissions_commission_id_seq")
    private Long commissionId;

    @Enumerated(EnumType.STRING)
    private CommissionType commissionType;

   	private int commissionAmount;

   	private double commissionPercentage;

    private Boolean active;

    private Instant createDate;
}
