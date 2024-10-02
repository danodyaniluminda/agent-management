package com.digibank.agentmanagement.web.dto.usermanagement.response;


import com.digibank.agentmanagement.domain.CustomerStatus;
import com.digibank.agentmanagement.domain.IDDocumentType;
import com.digibank.agentmanagement.domain.RegistrationType;
import com.digibank.agentmanagement.domain.locale;
import com.digibank.agentmanagement.web.dto.usermanagement.DocumentDTO;
import com.digibank.agentmanagement.web.dto.usermanagement.NotificationPreferenceDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletCustomerDetails {
    private Long customerId;

    private RegistrationType registrationType;

    private String firstName;

    private String lastName;

    private String bankCustomerId;

    private String countryCode;

    private String phoneNumber;

    private IDDocumentType idDocumentType;

    private String idDocumentNumber;

    private String idDocumentExpiryDate;

    private String emailAddress;

    private String uin;

    private boolean tcAccepted;

    private CustomerStatus status;

    private String iamId;

    private locale locale = com.digibank.agentmanagement.domain.locale.en;

    private String currencyCode;

    private String agentBankerPhoneNumber;

    private List<DocumentDTO> idDocument;

    private String address;

    private String cityOfResidence;

    private DocumentDTO selfieDocument;

    private String phoneNumberCountryCode;

    private NotificationPreferenceDTO customerPreference;

    private String dateOfBirth;

    private String registrationCompletedAt;

    private String activatedAt;
}
