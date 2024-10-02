package com.digibank.agentmanagement.domain.packagemanagement;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Table(name = "PackageOperationPermission")
public class PackageOperationPermission implements Serializable {

    private static final long serialVersionUID = 584861899224854005L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "packageAssetOperationPermissions_permission_id_seq")
    @SequenceGenerator(name = "packageAssetOperationPermissions_permission_id_seq", allocationSize = 1,
            sequenceName = "packageAssetOperationPermissions_permission_id_seq")
    private Long permissionId;

    private Instant effectiveFrom;

    private Instant effectiveTo;

    private Boolean active;

    private Instant createDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "operationId")
    private Operation operation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commissionId")
    private Commission commission;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "packageId")
    private AgentPackage agentPackage;
}
