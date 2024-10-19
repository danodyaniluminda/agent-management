package com.biapay.agentmanagement.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Document_ID_Info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DocumentIdInfo extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "businessInfos_DocumentInfo_id_seq")
    @SequenceGenerator(
            name = "businessInfos_DocumentInfo_id_seq",
            allocationSize = 1,
            sequenceName = "businessInfos_DocumentInfo_id_seq")
    private Long documentId;

    private String documentName;

    @Enumerated(EnumType.STRING)
    private IDDocumentType documentType;

    private String documentIdNumber;

    private LocalDate documentExpiryDate;

    private String documentFileName;

    private String documentFileNameOrignal;

    @Enumerated(EnumType.STRING)
    private LinkedModule linkedModule;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private AgentDetails agentDetails;

}
