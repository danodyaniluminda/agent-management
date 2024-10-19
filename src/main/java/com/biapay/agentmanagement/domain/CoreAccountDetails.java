package com.biapay.agentmanagement.domain;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "CoreAccountDetails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CoreAccountDetails extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "walletAccounts_WalletAccount_id_seq")
    @SequenceGenerator(
            name = "walletAccounts_WalletAccount_id_seq",
            allocationSize = 1,
            sequenceName = "walletAccounts_WalletAccount_id_seq")
    private Long id;

    private String accountId ;

    private AccountType accountType ;

}
