package com.digibank.agentmanagement.web.dto.accounttransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CashDepositWithdrawResponse {

    private String eventNo;
    private String eventDate;
    private BigDecimal amount;
    private BigDecimal fees;
    private String status;
    private String branchCode;
    private String accountNo;
    private String reason;
    private String opeCode;
    private String opeTitle;
    private String custId;
    private String custName;
    private String recipientName;
    private String recipientAccount;
    private BigDecimal trxId;
    private Object eventLikeModel;
    private String eventId;
    private String eventCode;
    private String trxAuthoriseCode;
    private String secretCode;
    private String secretCodeValidity;
    private String senderPhoneNo;
    private String recipientPhoneNo;
    private String referenceExterne;
    private String referenceExterne2;
    private String externalStatus;
    private String statusTrxInitialInCbs;
    private String statusTrxActualInCbs;
    private BigDecimal commissionTotale;
    private BigDecimal taxe;
    private BigDecimal commissionReseauPartenaire;
    private String dateComptableEventInCbs;
}
