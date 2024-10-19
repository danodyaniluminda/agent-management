package com.biapay.agentmanagement.web.dto;


import com.biapay.agentmanagement.domain.*;
import com.biapay.agentmanagement.web.dto.packagemanagement.LimitProfileDto;
import com.biapay.core.constant.enums.KycApprovalStatus;
import com.biapay.core.model.Industry;
import com.biapay.core.model.MerchantGroup;
import com.biapay.core.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import javax.persistence.Column;
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
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentDetailsDTO {

  private Long id;

  private String agentName;

  private String supervisorAgentId;

  private String supervisorAgentEmail;

  private boolean status;

  @Enumerated(EnumType.STRING)
  AgentStatus agentStatus;

  @Enumerated(EnumType.STRING)
  AgentType agentType;

  @Enumerated(EnumType.STRING)
  KycApprovalStatus kycApprovalStatus;

  private String walletAddress;

  private String email;

  private String iamId;
  private String phoneNo;

  private Boolean phoneVerified;
  private Boolean twoFactorStatus;

  private String phoneVerificationOTP;

  private Set<User> users;

  private User rootUser;
  private Industry industry;

  // Additional fields to complete Merchant registration workflow - end

  @Transient
  private String confirmPassword;

  @Transient private String role;

  @Transient private String planName;

  String referredByBusinessReferralCode;

  private String wallet;

  LocalDateTime registrationDateTime;
  private Set<MerchantGroup> merchantGroups;
}
