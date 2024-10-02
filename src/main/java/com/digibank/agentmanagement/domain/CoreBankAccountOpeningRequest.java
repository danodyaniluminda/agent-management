package com.digibank.agentmanagement.domain;

import com.digibank.agentmanagement.web.dto.RegistrationStatus;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "BankAccountOpeningRequests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CoreBankAccountOpeningRequest extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountRequests_Account_id_seq")
    @SequenceGenerator(
            name = "accountRequests_Account_id_seq",
            allocationSize = 1,
            sequenceName = "accountRequests_Account_id_seq")
    private Long id;

    private String firstName ;

    private String lastName ;

    private String mobileCountryCode ;

    private String phoneNumber ;

    private String emailAddress;

    private String idCardNumber;

    private String address;

    private String city;

    private String district;

    private String countryCode;

    private String dateOfBirth;

    private String zipCode;

    private String signature ;

    private RegistrationStatus registrationStatus ;

    private String pin;

    @Column(unique=true)
    private String uid ;
}
