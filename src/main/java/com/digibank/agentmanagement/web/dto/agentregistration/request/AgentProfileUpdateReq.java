package com.digibank.agentmanagement.web.dto.agentregistration.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentProfileUpdateReq {
    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumberCountryCode;

    private String phoneNumber;

    private MultipartFile selfieDocumentFile;

    private String mfaToken;
}
