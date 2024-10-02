package com.digibank.agentmanagement.domain.packagemanagement;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Table(name = "AccessInformation")
public class AccessInformation implements Serializable {

    private static final long serialVersionUID = 584861899224854008L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accessInformation_accessInfo_id_seq")
    @SequenceGenerator(
            name = "accessInformation_accessInfo_id_seq",
            allocationSize = 1,
            sequenceName = "accessInformation_accessInfo_id_seq")
    private Long accessInfoId;

    private String userName;

    private String browser;

    private Instant accessDate;

}
