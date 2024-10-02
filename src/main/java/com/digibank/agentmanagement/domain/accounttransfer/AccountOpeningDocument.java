package com.digibank.agentmanagement.domain.accounttransfer;

import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.DocumentIdInfo;
import com.digibank.agentmanagement.domain.IDDocumentType;
import com.digibank.agentmanagement.domain.LinkedModule;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "AccountOpeningDocument")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOpeningDocument implements Serializable {

    private static final long serialVersionUID = 584861899224854012L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountOpeningDocuments_accountOpeningDocumentId_seq")
    @SequenceGenerator(name = "accountOpeningDocuments_accountOpeningDocumentId_seq", allocationSize = 1,
            sequenceName = "accountOpeningDocuments_accountOpeningDocumentId_seq")
    private Long accountOpeningDocumentId;

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

    @ManyToOne
    @JoinColumn(name = "account_id", nullable=true)
    private PersonAccount personAccount;
}
