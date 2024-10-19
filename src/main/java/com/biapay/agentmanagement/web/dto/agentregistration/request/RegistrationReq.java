package com.biapay.agentmanagement.web.dto.agentregistration.request;


import com.biapay.agentmanagement.domain.AgentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RegistrationReq {

  private Long id;

  private String agentName;
  private AgentType agentType;
  private String email;
  private String password;
  private String confirmPassword;
  private String phoneNo;
  private String firstName;
  private String lastName;
  private Boolean twoFactorStatus;
  private String wallet;
  private String superAgentId;
  private String superAgentPhone;
}
