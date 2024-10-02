package com.digibank.agentmanagement.domain.packagemanagement;

import com.digibank.agentmanagement.domain.AgentDetails;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "UserRole")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 584861899224854011L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userRoles_userRole_id_seq")
    @SequenceGenerator(
            name = "userRoles_userRole_id_seq",
            allocationSize = 1,
            sequenceName = "userRoles_userRole_id_seq")
    private Long userRoleId;

    @Column(unique=true)
    private String name;

    private String displayName;

    private String description;

    private boolean defaultRole;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agentId")
    private AgentDetails agentDetails;
}
