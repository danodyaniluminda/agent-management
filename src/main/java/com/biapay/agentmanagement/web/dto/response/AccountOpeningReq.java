package com.biapay.agentmanagement.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountOpeningReq {

    private Channels channelId;

    private RegistrationType registrationType;


    private String firstName ;

    private String lastName ;

    private String mobileCountryCode ;

    private String phoneNumber ;

    private String zipCode ;

    private String emailAddress;

    private String idCardNumber;

    private List<MultipartFile> identityDocuments;

    private LocalDate identityDocumentsExpiryDate ;

    private String address;

    private String city;

    private String district;

    private String countryCode;

    private LocalDate dateOfBirth;

    private String signature ;

    private String accountReference ;

    private String pin;

    // device related details in request
    private String deviceUid;

    private String osName;

    private String osVersion;

    private String deviceManufacturer;

    private String osFirmwareBuildVersion;

    private String deviceModel;

    private String macAddress;

    private String imeiList;

    private String firebaseDeviceToken;

    private String deviceInfoSignature;

    private String rootedDevice;

}