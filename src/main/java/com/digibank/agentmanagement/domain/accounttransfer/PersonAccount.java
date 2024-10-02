package com.digibank.agentmanagement.domain.accounttransfer;

import com.digibank.agentmanagement.domain.DocumentIdInfo;
import com.digibank.agentmanagement.domain.IDDocumentType;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PersonAccount")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonAccount implements Serializable {

    private static final long serialVersionUID = 584861899224854012L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personAccounts_accountId_seq")
    @SequenceGenerator(name = "personAccounts_accountId_seq", allocationSize = 1,
            sequenceName = "personAccounts_accountId_seq")
    private Long accountId;

    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String address;
    private String cityOfResidence;
    private String phoneNumberCountryCode;
    private String phoneNumber;
    private String emailAddress;
    private String countryCode;

    @Enumerated(EnumType.STRING)
    private IDDocumentType idDocumentType;
    private String idDocumentNumber;
    private String idDocumentExpiryDate;

    private String uin;
    private boolean tcAccepted;
    private String locale;
    private String currencyCode;
    private String agentBankerPhoneNumber;
}
