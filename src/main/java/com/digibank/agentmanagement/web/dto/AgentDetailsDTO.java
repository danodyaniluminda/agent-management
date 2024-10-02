package com.digibank.agentmanagement.web.dto;


import com.digibank.agentmanagement.domain.*;
import com.digibank.agentmanagement.web.dto.packagemanagement.LimitProfileDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
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


    private Long agentId;

    private RegistrationType registrationType;

    private String firstName;

    private String lastName;

    private String registrationChannel;

    private String registrationSubChannel;

    private AgentType agentType;

    private String agentBusinessName;

    private BusinessType businessType;

    private String countryCode;

    private String phoneNo;

    private locale locale;

    private String phoneNumberCountryCode;

    private String mobileOperator;

    private List<DocumentIdInfoDTO> idDocuments;

    private DocumentIdInfoDTO selfieDocument;

    private String agentEmailAddress;

    private AgentStatus status;

    private String bankCustomerId;

    @JsonIgnore
    private String UID;

    @JsonIgnore
    private String bioData;

    private String currency;

    private String gender;

    @JsonIgnore
    private String agentGroupId;

    private LocalDateTime registrationAppDateTime;

    private String agentRegisteredBy;

    private LocalDate agentDOB;

    @JsonIgnore
    private String dualFactorRequired;

    @JsonIgnore
    private String fingerPrintData;

    private String agentBusinessAddress;

    private String agentBusinessCity;

    private String appliedForRegistrationAt;

    private String trustLevel;

    private String businessRegistrationDate;

    private String tradeRegistrationNumber;

    private String taxPayerNumber;

    private String superAgentEmail;

    private String superAgentId;

    private String agentLinkedTo;

    private List<DocumentIdInfoDTO> proofOfAddress;


    private String websiteLink;

    private String city;

    private String address;

    private String longitude;

    private String latitude;


    private List<LimitProfileDto> agentLimitProfiles;
}
