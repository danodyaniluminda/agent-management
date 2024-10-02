package com.digibank.agentmanagement.domain.packagemanagement;


import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.packagemanagement.UserRole;
import com.digibank.agentmanagement.enums.UserStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "AgentUser")
public class AgentUser implements Serializable {

    private static final long serialVersionUID = 5848618992248540007L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agentUsers_agentUser_id_seq")
    @SequenceGenerator(
            name = "agentUsers_agentUser_id_seq",
            allocationSize = 1,
            sequenceName = "agentUsers_agentUser_id_seq")
    private Long agentUserId;

    private String name;

    @Column(unique=true)
    private String userName;

    @Column(unique=true)
    private String email;

    @Column(unique=true)
    private String mobileNumber;

    private String password;

    private String userIamId;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userRoleId")
    private UserRole userRole;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agentId")
    private AgentDetails agentDetails;
}
