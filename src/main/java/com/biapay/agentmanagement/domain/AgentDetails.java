package com.biapay.agentmanagement.domain;

import com.biapay.core.constant.enums.KycApprovalStatus;
import com.biapay.core.constant.enums.MerchantStatus;
import com.biapay.core.model.Industry;
import com.biapay.core.model.MerchantGroup;
import com.biapay.core.model.User;
import com.biapay.core.model.enums.Locale;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;


@Entity
@Table(name = "AgentDetails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentDetails extends AbstractAuditingEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_seq")
    @SequenceGenerator(name = "agent_seq", allocationSize = 1, sequenceName = "agent_seq")
    private Long id;

    @Column(name = "agent_name")
    private String agentName;

    @Column(name = "supervisor_agent_id", nullable = true)
    private String supervisorAgentId;

    @Column(name = "supervisor_agent_email", nullable = true)
    private String supervisorAgentEmail;

    @Column(name = "status")
    private boolean status;

    @Column(name = "agent_status")
    @Enumerated(EnumType.STRING)
    AgentStatus agentStatus;

    @Column(name = "agent_type")
    @Enumerated(EnumType.STRING)
    AgentType agentType;

    @Column(name = "kyc_approval_status")
    @Enumerated(EnumType.STRING)
    KycApprovalStatus kycApprovalStatus;

    @Column(name = "wallet_address")
    private String walletAddress;

    @Column(name = "email")
    private String email;

    @Column(name = "iam_id")
    private String iamId;

    @Column(name = "phone_no", nullable = false)
    private String phoneNo;

    @Column(name = "phone_verified")
    private Boolean phoneVerified;

    @Column(name = "two_factor_status")
    private Boolean twoFactorStatus;

    @Column(name = "phone_verification_otp")
    private String phoneVerificationOTP;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<User> users;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "root_user_id")
    @JsonBackReference
    private User rootUser;

    // Additional fields to complete Merchant registration workflow - start

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "industry_industry_id", nullable = true)
    private Industry industry;

    // Additional fields to complete Merchant registration workflow - end

    @Transient
    private String confirmPassword;

    @Transient private String role;

    @Transient private String planName;

    // additional fields for the referral program
    @Column(name = "referred_by_business_referral_code", unique = true, length = 256)
    String referredByBusinessReferralCode;

    private String wallet;

    @Column(name = "registration_date", unique = true, length = 256)
    LocalDateTime registrationDateTime;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(mappedBy = "merchants", fetch = FetchType.EAGER)
    private Set<MerchantGroup> merchantGroups;
}
