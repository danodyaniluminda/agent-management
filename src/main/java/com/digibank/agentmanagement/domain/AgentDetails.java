package com.digibank.agentmanagement.domain;


import com.digibank.agentmanagement.domain.packagemanagement.AgentPackage;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "AgentDetails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentDetails extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agents_agent_id_seq")
    @SequenceGenerator(
            name = "agents_agent_id_seq",
            allocationSize = 1,
            sequenceName = "agents_agent_id_seq")
    private Long agentId;

    @Enumerated(EnumType.STRING)
    private RegistrationType registrationType;

    private String firstName;

    private String lastName;

    private String registrationChannel;

    private String registrationSubChannel;


    @Enumerated(EnumType.STRING)
    private AgentType agentType;

    private String agentBusinessName;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    private String countryCode;

    private String phoneNumberCountryCode;

    @Enumerated(EnumType.STRING)
    private locale locale;

    @Column(unique = true)
    private String phoneNo;

    private String mobileOperator;


    private String agentEmailAddress;


    @Column(unique = true)
    private String iamId;

    private String bioData;

    private String currency;

    private String gender;

    //@OneToOne(fetch = FetchType.EAGER)
//    @ManyToOne(fetch = FetchType.EAGER )
//    @JoinColumn(name = "termConditionId" )
//    private TermsConditions termsConditions;

    @Enumerated(EnumType.STRING)
    private AgentStatus status;

    private String agentCurrentStateReason;

    private String approvedBy;

    private String kycUpdatedBy;

    private LocalDateTime registrationDateTime;

    private LocalDateTime registrationAppDateTime;

    private String agentLinkedTo;

    private String agentRegisteredBy;


    private LocalDate agentDOB;

    private String superAgentId;

    private String linkedAccountNumber;

    private String bankCustomerId;


    //@OneToOne(fetch = FetchType.EAGER)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "packageId")
    private AgentPackage agentPackage;

    private String dualFactorRequired;

    private String fingerPrintData;

    private String agentGroupId;

    private String agentBusinessAddress;

    private String agentBusinessCity;

    private String appliedForRegistrationAt;

    private String activatedAt;

    private String trustLevel;

    private String loginFailAttempt;

    private String businessRegistrationDate;

    private String tradeRegistrationNumber;

    private String taxPayerNumber;

    private String city;

    private String address;

    private String longitutde;

    private String latitude;


    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "agent_id")
    @Where(clause = "document_type='ID_DOCUMENT'")
    private Set<DocumentIdInfo> idDocuments = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "document_type='SELFIE_DOCUMENT'")
    @JoinColumn(name = "agent_id")
    private Set<DocumentIdInfo> selfieDocuments = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "agent_id")
    @Where(clause = "document_type='ADDRESS_PROOF'")
    private Set<DocumentIdInfo> proofOfAddress = new HashSet<>();

    private String websiteLink;


    public MFAChannel getMFAChannel() {
        MFAChannel mfaChannel = null;
        if (this.getAgentEmailAddress() != null
                && this.getPhoneNo() != null) {
            mfaChannel = MFAChannel.BOTH;
        } else if (this.getAgentEmailAddress() != null) {
            mfaChannel = MFAChannel.EMAIL;
        } else if (this.getPhoneNo() != null && this.getPhoneNumberCountryCode() != null) {
            mfaChannel = MFAChannel.SMS;
        }

        return mfaChannel;
    }

    public String getFullName() {
        if (this.firstName != null && this.lastName != null) {
            return String.join(this.firstName, this.lastName, " ");
        }
        if (this.firstName != null) {
            return this.firstName;
        }
        if (this.lastName != null) {
            return this.lastName;
        }
        return "";
    }

    public void addIdDocument(DocumentIdInfo document) {
        if (idDocuments == null)
            idDocuments = new HashSet<>();
        idDocuments.add(document);
        document.setAgentDetails(this);
    }

    public void addAddressProofDocument(DocumentIdInfo document) {
        if (proofOfAddress == null)
            proofOfAddress = new HashSet<>();
        proofOfAddress.add(document);
        document.setAgentDetails(this);
    }

    public void removeAddressProofDocument(DocumentIdInfo document) {
        proofOfAddress.remove(document);
        document.setAgentDetails(null);
    }

    public void removeIdDocument(DocumentIdInfo document) {
        idDocuments.remove(document);
        document.setAgentDetails(null);
    }

    public void addSelfieDocument(DocumentIdInfo document) {
        if (selfieDocuments == null)
            selfieDocuments = new HashSet<DocumentIdInfo>();
        selfieDocuments.add(document);
        document.setAgentDetails(this);
    }


    public void removeSelfieDocument(DocumentIdInfo document) {
        selfieDocuments.remove(document);
        document.setAgentDetails(null);
    }


}
