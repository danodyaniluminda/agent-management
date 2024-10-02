package com.digibank.agentmanagement.web.dto.agentregistration.request;


import com.digibank.agentmanagement.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RegistrationReq {

     private RegistrationType registrationType;

     private String firstName;

     private String lastName;

     private String registrationChannel;

     private String registrationSubChannel;

     private AgentType agentType;


     private BusinessType busincessType;

     private  String countryCode;

     private String phoneNo;

     private locale locale ;

     private String phoneNumberCountryCode;

     private String mobileOperator;

     private IDDocumentType idDocumentType;

     private String idDocumentName;

     private String idDocumentIdNumber;

     private LocalDate idExpiryDate;

    private List<MultipartFile> idDocumentImages;



     private String agentEmailAddress;

    private String bioData;

    private MultipartFile agentPhoto;

     private String currency;

     private String gender;

    private LocalDateTime registrationAppDateTime;

    private String agentRegisteredBy;

    private LocalDate agentDOB;

    private String appliedForRegistrationAt ;

    private String bankCustomerId;

    private String superAgentPhone;

    private String agentBusinessName;

    private String agentBusinessAddress ;

    private String agentBusinessCity ;

    private String businessRegistrationDate;

    private String tradeRegistrationNumber;

    private String taxPayerNumber ;

    private List<MultipartFile> proofOfAddress;

    private String websiteLink;

    private String city ;

    private String address ;

    private String longitutde;

    private String latitude;

    // fields for Agent Member

    private Long packageId;

    private AgentStatus status;

    private String password;



}
