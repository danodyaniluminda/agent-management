package com.digibank.agentmanagement.domain.packagemanagement;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Table(name = "Operation")
public class Operation implements Serializable {

    private static final long serialVersionUID = 584861899224854004L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operations_operation_id_seq")
    @SequenceGenerator(name = "operations_operation_id_seq", allocationSize = 1, sequenceName = "operations_operation_id_seq")
    private Long operationId;

    private String name;

    private Boolean active;

    private Instant createDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assetId")
    private Asset asset;
}
