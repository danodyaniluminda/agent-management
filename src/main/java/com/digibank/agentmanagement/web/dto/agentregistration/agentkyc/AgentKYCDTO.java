package com.digibank.agentmanagement.web.dto.agentregistration.agentkyc;


import com.digibank.agentmanagement.domain.IDDocumentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentKYCDTO {
    private LocalDate dateOfBirth;

    private IDDocumentType idDocumentType;

    private String idDocumentNumber;

    private String idDocumentName;

    private LocalDate idDocumentExpiryDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<MultipartFile> idDocumentFile = new ArrayList<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<MultipartFile> addressProof = new ArrayList<>();

    private String city ;

    private String address ;

    private String mfaToken;

    private String mobileNumber;

    private String email;

    private String longitude;

    private String latitude;

}
