package com.biapay.agentmanagement.web.dto.agentregistration.agentkyc;

import com.biapay.agentmanagement.web.dto.DocumentIdInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentKYCResDTO {
    private LocalDate dateOfBirth;

    private List<DocumentIdInfoDTO> idDocuments;

    private List<DocumentIdInfoDTO> proofOfAddress;

    private String emailAddress;

    private String agentIamID;

    private String agentStatus;

    private String phoneNo;

    private String city ;

    private String address ;

    private String longitude ;

    private String latitude ;


}
