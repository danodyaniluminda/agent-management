package com.digibank.agentmanagement.domain.packagemanagement;

import com.digibank.agentmanagement.domain.AgentType;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "AgentPackage")
public class AgentPackage implements Serializable {

    private static final long serialVersionUID = 584861899224854000L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agentPackages_package_id_seq")
    @SequenceGenerator(name = "agentPackages_package_id_seq", allocationSize = 1, sequenceName = "agentPackages_package_id_seq")
    private Long packageId;

    private String name;

    @Enumerated(EnumType.STRING)
    private PackageType packageType;

    @Enumerated(EnumType.STRING)
    private AgentType agentType;

    private Boolean isDefault;

    private Boolean isFeatured;

    private String channel;

    @Enumerated(EnumType.STRING)
    private SettlementPeriod settlementPeriod;

    private String planPrice;

    private Boolean active;

    private Instant createDate;

//    @Transient
//    List<PackageOperationPermission> packageOperationPermissions;
//
//    @Transient
//    List<PackageCurrencyLimit> packageCurrencyLimits;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AgentPackage that = (AgentPackage) o;
        return Objects.equals(packageId, that.packageId) && Objects.equals(name, that.name) && Objects.equals(packageType, that.packageType)
                && Objects.equals(agentType, that.agentType) && Objects.equals(isDefault, that.isDefault) && Objects.equals(isFeatured, that.isFeatured)
                && Objects.equals(channel, that.channel) && Objects.equals(settlementPeriod, that.settlementPeriod) && Objects.equals(planPrice, that.planPrice)
                && Objects.equals(active, that.active);
    }

    @Override
    public int hashCode() {
        return 31 * packageId.intValue();
    }
}
