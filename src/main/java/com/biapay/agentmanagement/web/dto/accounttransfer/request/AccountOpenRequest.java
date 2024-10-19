package com.biapay.agentmanagement.web.dto.accounttransfer.request;

import com.biapay.agentmanagement.domain.IDDocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccountOpenRequest {

    @NotNull
    private String firstName;   //:Clovis
    @NotNull
    private String lastName;    //:Clovis
    @NotNull
    private String dateOfBirth; //:1985-12-12
    @NotNull
    private String address; //:address
    @NotNull
    private String cityOfResidence; //:cityOfResidence
    @NotNull
    private String phoneNumberCountryCode;  //:+237
    @NotNull
    private String phoneNumber; //:123456784
    @NotNull
    private String emailAddress;    //:mayuran19+15@gmail.com
    @NotNull
    private String countryCode; //:CM
    @NotNull
    private IDDocumentType idDocumentType;  //:ID_CARD
    @NotNull
    private String idDocumentNumber;    //:ABC123
    @NotNull
    private String idDocumentExpiryDate;    //:2021-01-01
    @NotNull
    private List<MultipartFile> identityDocuments;
    @NotNull
    private String uin; // :123456
    @NotNull
    private boolean tcAccepted; //:true
    @NotNull
    private String locale;  //:en
    @NotNull
    private String currencyCode;    //:SGD
    @NotNull
    private String agentBankerPhoneNumber;  //:test@test.com
    @NotNull
    private MultipartFile selfiePhoto;

}
