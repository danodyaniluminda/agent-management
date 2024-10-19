package com.biapay.agentmanagement.domain;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "AgentLinkingRequest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentLinkingRequest extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agentLinkingDetails_AgentLinkingDetail_id_seq")
    @SequenceGenerator(
            name = "agentLinkingDetails_AgentLinkingDetail_id_seq",
            allocationSize = 1,
            sequenceName = "agentLinkingDetails_AgentLinkingDetail_id_seq")
    private Long id;


    private String parentAgentId ;

    @Column(unique=true)
    private String childAgentId;

    @Enumerated(EnumType.STRING)
    private AgentLinkingStatus linkingStatus;

}
