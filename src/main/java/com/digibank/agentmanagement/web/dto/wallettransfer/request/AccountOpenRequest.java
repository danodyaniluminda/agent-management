/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digibank.agentmanagement.web.dto.wallettransfer.request;

import com.digibank.agentmanagement.domain.IDDocumentType;
import com.digibank.agentmanagement.domain.RegistrationType;
import com.digibank.agentmanagement.domain.locale;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ali-r
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountOpenRequest {
    
  @NotNull private RegistrationType registrationType;

  private String bankCustomerId;

  private String firstName;

  private String lastName;

  @NotNull private String phoneNumberCountryCode;

  @NotNull private String countryCode;

  @NotNull private String phoneNumber;

  @NotNull private IDDocumentType idDocumentType;

  private List<MultipartFile> idDocumentFile;

  @NotNull private String idDocumentNumber;

  @NotNull private LocalDate idDocumentExpiryDate;

  private String emailAddress;

  private String uin;

  private boolean tcAccepted;

  @NotNull private locale locale;

  private String currencyCode;

  private String agentBankerPhoneNumber;

  private MultipartFile selfieDocumentFile;

  private LocalDate dateOfBirth;

  private String address;

  private String cityOfResidence;
    
}
