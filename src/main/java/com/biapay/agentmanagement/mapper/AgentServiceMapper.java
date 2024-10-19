package com.biapay.agentmanagement.mapper;

import com.biapay.agentmanagement.domain.*;
import com.biapay.agentmanagement.web.dto.*;
import com.biapay.agentmanagement.web.dto.agentregistration.agentkyc.AgentKYCDTO;
import com.biapay.agentmanagement.web.dto.agentregistration.agentkyc.AgentKYCResDTO;
import com.biapay.agentmanagement.web.dto.agentregistration.request.RegistrationReq;
import com.biapay.agentmanagement.web.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AgentServiceMapper {

    AgentDetails fromAgentRegistrationReq(RegistrationReq registrationReq);

    DocumentIdInfoDTO fromDocumentIdInfoEntity (DocumentIdInfo documentIdInfo);

    String fromMultiFileToStringFileName (MultipartFile file);

    AgentDetailsResponse fromAgentDetailsToAgentDetailsResponse(AgentDetails agentDetails);

    DeviceInfo fromCustomerRequestToDeviceInfo(AccountOpeningReq walletAccountOpeningReq);

    GenericReceiptDataResponse setReceiptData(BalanceInquiryRespnse balanceInquiryRespnse);

    CoreBankAccountOpeningRequest fromAccoutnOpeningRequestToEntity(AccountOpeningReq accountOpeningReq);

    public List<AgentDetailsResponse> fromAgentDetailsToAgentDetailsResponses(List<AgentDetails> agentDetailsList);

    void updateAgent(@MappingTarget AgentDetails agent, AgentKYCDTO agentKYCDTO);

    public AgentDetailsDTO fromAgentDetailsToDTO(AgentDetails agent);

    public AgentKYCResDTO fromAgentDetailsToKYCDTO(AgentDetails agent);

    AgentLinkingRequestDTO fromAgentDetailsToAgentLinkingRequestDTO(AgentDetails agent);
}
