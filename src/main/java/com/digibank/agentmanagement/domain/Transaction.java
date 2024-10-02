package com.digibank.agentmanagement.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Transaction_Details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_transaction_id_seq")
    @SequenceGenerator(
            name = "transactions_transaction_id_seq",
            allocationSize = 1,
            sequenceName = "transactions_transaction_id_seq")
    private Long transactionId;

    private String transactionKey;
    
    private String fromCustomerId ; 
    
    private String toCustomerId;
    
    private String customerWalletId;

    private String superAgentWalletId;

    private String agentWalletId;

    private String memberWalletId;

    private String fromAccount;

    private String toAccount;

    private String commissionDebit;

    private String commissionCredit;

    private String commissionDebitWallet;

    private String commissionCreditWallet;

    private String ledgerCredit;

    private String ledgerDebit;

    private String transactionType;
    
    private LocalDate transactionDate;

    private LocalTime transactionTime;
    
    private String transactionDescription;
    
    private String hostResponseCode;

    private String transactionStatusCode;

    private String transactionPerformedBy;

    private String transactionApprovedBy;

    private String customerAuthorizationDetail;

    private String agentAuthorizationDetail;

    private String transactionChannel;

    private String memberAgentCommission;

    private String agentCommission;

    private String superAgentCommission;
    
    private String jsonResponse;

    private String errorCode;

    private Long agentId;

    private String currencyName;

    private BigDecimal amount;
}
