package com.digibank.agentmanagement.domain.packagemanagement;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Table(name = "Asset")
public class Asset implements Serializable {

    private static final long serialVersionUID = 584861899224854001L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assets_asset_id_seq")
    @SequenceGenerator(name = "assets_asset_id_seq", allocationSize = 1, sequenceName = "assets_asset_id_seq")
    private Long assetId;

    @Column(unique=true)
    private String name;

    private Boolean active;

    private Instant createDate;
}
