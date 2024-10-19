package com.biapay.agentmanagement.web.dto.agentregistration.agentkyc;


import com.biapay.agentmanagement.enums.KYCApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentKYCApprovalReq {

    private KYCApprovalStatus kycApprovalStatus;
}
