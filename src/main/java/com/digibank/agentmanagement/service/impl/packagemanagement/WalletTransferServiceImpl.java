package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.deserializer.responses.ExistingCustomerRegistration;
import com.digibank.agentmanagement.domain.*;
import com.digibank.agentmanagement.exception.AccountNotActiveException;
import com.digibank.agentmanagement.exception.BadRequestException;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.service.*;
import com.digibank.agentmanagement.service.packagemanagement.*;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.dto.TransferCommissionDto;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.CustomerDetailsResponse;
import com.digibank.agentmanagement.web.dto.response.GenericTransactionRequest;
import com.digibank.agentmanagement.web.dto.response.TransactionRes;
import com.digibank.agentmanagement.web.dto.usermanagement.response.WalletCustomerDetails;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.*;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@AllArgsConstructor
public class WalletTransferServiceImpl implements WalletTransferService {

    private final AgentManagementUtils agentManagementUtils;
    private final AgentDetailsService agentDetailsService;
    private final OperationService operationService;
    private final CurrencyService currencyService;
    private final AgentLimitProfileService agentLimitProfileService;
    private final PackageOperationPermissionService packageOperationPermissionService;
    private final PackageCurrencyLimitService packageCurrencyLimitService;
    private final TransactionService transactionService;
    private final AssetService assetService;
    private final WalletService walletService;
    private final UserManagementAdapterService userManagementAdapterService;
    private final NotificationService notificationService;
    private final BankAdapterService bankAdapterService;

    @Override
    public ExistingCustomerRegistration walletAccountOpeningRequest(KeycloakAuthenticationToken token, AccountOpenRequest accountOpenRequest) {
        return walletService.customerRegistration(accountOpenRequest);
    }

    @Override
    public void verifyPin(VerifyPinRequest verifyPinRequest) {
        walletService.verifyPin(verifyPinRequest);
    }

    @Override
    public void resendPin(ResendPinRequest resendPinRequest) {
        walletService.resendPin(resendPinRequest);
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        walletService.setPassword(resetPasswordRequest);
    }

    @Override
    public WalletHistoryPageResponse AgentWalletHistory(KeycloakAuthenticationToken token, WalletHistoryRequest walletHistoryRequest) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
        walletHistoryRequest.setUserId(agentDetails.getAgentId() + "");
        walletHistoryRequest.setUserType("AGENT");
        walletHistoryRequest.setCurrencyCodes(agentDetails.getCurrency());

        return walletService.walletHistory(token, walletHistoryRequest);
    }

    @Override
    public WalletHistoryPageResponse walletHistory(KeycloakAuthenticationToken token, WalletHistoryRequest walletHistoryRequest) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
//        if (agentDetails.getStatus() != AgentStatus.ACTIVE) {
//            throw new AccountNotActiveException(ApiError.AGENT_NOT_ACTIVE);
//        }

        WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, walletHistoryRequest.getMobileNumber());

        boolean isValidated = notificationService.validateMFACore(walletCustomerDetails.getPhoneNumber(), walletCustomerDetails.getEmailAddress()
                , agentDetails.getAgentId(), walletHistoryRequest.getMfaToken(), "AGENT");

        if (!isValidated) {
            throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
        }


        walletHistoryRequest.setUserType("CUSTOMER");
        walletHistoryRequest.setUserId(walletCustomerDetails.getCustomerId().toString());
        WalletHistoryPageResponse walletHistoryPageResponse = walletService.walletHistory(token, walletHistoryRequest);

        if (walletHistoryRequest.getSendViaEmail() == true && walletHistoryPageResponse.getContent().size() != 0) {
            AtomicReference<String> htmlTemplate = new AtomicReference<>("<table style=\"width:100%,border:1px solid black\"><tr style=\"border:1px solid black\"><th style=\"border:1px solid black\">Date</th><th style=\"border:1px solid black\">Type</th><th style=\"border:1px solid black\">Amount</th><th style=\"border:1px solid black\">Reference</th><th style=\"border:1px solid black\">Receiver Name</th></tr><tr style=\"border:1px solid black\">");
            walletHistoryPageResponse.getContent().forEach(walletHistoryResponse -> {
                htmlTemplate.set(htmlTemplate + "<td style=\"border:1px solid black\">" + walletHistoryResponse.getWalletTransaction().getCreatedDate().toString().split("T")[0] + "</td>");
                htmlTemplate.set(htmlTemplate + "<td style=\"border:1px solid black\">" + walletHistoryResponse.getWalletTransaction().getWalletTransactionType() + "</td>");
                htmlTemplate.set(htmlTemplate + "<td style=\"border:1px solid black\">" + walletHistoryResponse.getWalletTransaction().getAmount() + "</td>");
                htmlTemplate.set(htmlTemplate + "<td style=\"border:1px solid black\">" + walletHistoryResponse.getWalletTransaction().getTransactionReference() + "</td>");
                htmlTemplate.set(htmlTemplate + "<td style=\"border:1px solid black\">" + walletHistoryResponse.getWalletTransaction().getToWalletUserName() + "</td>");
                htmlTemplate.set(htmlTemplate + "</tr>");
            });
            htmlTemplate.set(htmlTemplate + "</table>");


            notificationService.sendEmailNotification(agentDetails, MFAChannel.EMAIL, "WALLET_STATEMENT", "WALLET_STATEMENT", walletCustomerDetails.getEmailAddress(), walletCustomerDetails.getBankCustomerId(),
                    Map.of("wallet_id", walletCustomerDetails.getPhoneNumber(), "startdate", walletHistoryRequest.getFromDate().toString(), "enddate", walletHistoryRequest.getToDate().toString(), "statement", htmlTemplate.get()), false, true, false);
        }

        return walletHistoryPageResponse;
    }

    @Override
    public List<CustomerBalanceResponse> walletBalance(KeycloakAuthenticationToken token, WalletBalanceRequest walletBalanceRequest) {

        // validate OTP
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
        WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, walletBalanceRequest.getMobileNumber());

        boolean isValidated = notificationService.validateMFACore(walletCustomerDetails.getPhoneNumber(), walletCustomerDetails.getEmailAddress()
                , agentDetails.getAgentId(), walletBalanceRequest.getMfaToken(), "AGENT");

        if (!isValidated) {
            throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
        }

        List<CustomerBalanceResponse> balanceResponses = walletService.walletBalance(token, "CUSTOMER", walletCustomerDetails.getCustomerId().toString());

        if (walletBalanceRequest.getSendType().equals("SMS")) {
            balanceResponses.forEach(customerBalanceResponse -> {
                if (customerBalanceResponse.getCurrencyCode().equals("XAF")) {
                    notificationService.sendSMSNotification(agentDetails, MFAChannel.SMS,
                            "WALLET_BALANCE", "WALLET_BALANCE", walletCustomerDetails.getPhoneNumber(), walletCustomerDetails.getBankCustomerId(), Map.of("wallet_id", walletCustomerDetails.getPhoneNumber(), "amount", customerBalanceResponse.getBalance().toString()),
                            false, false, true);
                }
            });
        } else if (walletBalanceRequest.getSendType().equals("EMAIL")) {
            balanceResponses.forEach(customerBalanceResponse -> {
                if (customerBalanceResponse.getCurrencyCode().equals("XAF")) {
                    notificationService.sendEmailNotification(agentDetails, MFAChannel.EMAIL,
                            "WALLET_BALANCE", "WALLET_BALANCE", walletCustomerDetails.getEmailAddress(), walletCustomerDetails.getBankCustomerId(), Map.of("wallet_id", walletCustomerDetails.getPhoneNumber(), "amount", customerBalanceResponse.getBalance().toString()),
                            false, true, false);
                }
            });
        } else if (walletBalanceRequest.getSendType().equals("BOTH")) {

            balanceResponses.forEach(customerBalanceResponse -> {
                if (customerBalanceResponse.getCurrencyCode().equals("XAF")) {
                    notificationService.sendSMSNotification(agentDetails, MFAChannel.SMS,
                            "WALLET_BALANCE", "WALLET_BALANCE", walletCustomerDetails.getPhoneNumber(), walletCustomerDetails.getBankCustomerId(), Map.of("wallet_id", walletCustomerDetails.getPhoneNumber(), "amount", customerBalanceResponse.getBalance().toString()),
                            false, false, true);

                    notificationService.sendEmailNotification(agentDetails, MFAChannel.EMAIL,
                            "WALLET_BALANCE", "WALLET_BALANCE", walletCustomerDetails.getEmailAddress(), walletCustomerDetails.getBankCustomerId(), Map.of("wallet_id", walletCustomerDetails.getPhoneNumber(), "amount", customerBalanceResponse.getBalance().toString()),
                            false, true, false);
                }
            });

        }

        return balanceResponses;
    }

    @Override
    public TransactionRes walletToAFB(GenericTransactionRequest transactionRequest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransactionRes walletToWallet(GenericTransactionRequest transactionRequest) {
        return null;
    }

    @Override
    public WalletCashInResponse walletCashIn(String uuid, KeycloakAuthenticationToken token, WalletCashInRequest walletCashInRequest) {
        log.info("{} - cashDepositToAccount - {}", uuid, walletCashInRequest);
        LocalDateTime todayDateTime = LocalDateTime.now();
        String loggedInUserMobileNumber = AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token);
        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(loggedInUserMobileNumber);
        log.info("{} - agent found", uuid);
        if (!agentDetails.getAgentPackage().getActive()) {
            throw new ObjectNotFoundException(ApiError.PACKAGE_NOT_ACTIVE);
        }
        log.info("{} - agent is active", uuid);
        if (agentDetails.getAgentType() == AgentType.AGENT_MEMBER) {
            log.info("{} - Agent_Member are not allowed to perform Account based Transactions", uuid);
            throw new BadRequestException(ApiError.PERMISSION_DENIED);
        }


        CustomerDetailsResponse customerDetailsResponse = bankAdapterService.validateCustomer(token, walletCashInRequest.getBankCustomerId());
        boolean isValidated = notificationService.validateMFACore(customerDetailsResponse.getPhoneNumber(), customerDetailsResponse.getEmail()
                , agentDetails.getAgentId(), walletCashInRequest.getMfaToken(), "AGENT");

        if (!isValidated) {
            throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
        }
        walletCashInRequest.setUserType("AGENT");
        walletCashInRequest.setUserId(agentDetails.getAgentId().toString());
//        if(walletCashInRequest.getUserType().equals("AGENT")){
//            walletCashInRequest.setUserId(agentDetails.getAgentId().toString());
//
//        }
//        else if(walletCashInRequest.getUserType().equals("CUSTOMER")){
//            WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token , walletCashInRequest.getUserId());
//            walletCashInRequest.setUserId(walletCustomerDetails.getCustomerId().toString());
//        }
//        else{
//            throw new BadRequestException("INVALID USER TYEP");
//        }


        //walletCashInRequest.setReceiver(walletCashInRequest.getDebtorBankAccountNumber());
        //walletCashInRequest.setSender(agentDetails.getLinkedAccountNumber());

        TransferCommissionDto transferCommissionDto = agentManagementUtils.checkAgentLimitsForTransaction(uuid, "WALLET", walletCashInRequest.getType(),
                walletCashInRequest.getCurrencyName(), agentDetails, walletCashInRequest.getAmount(), walletCashInRequest.getFee(), todayDateTime);
        log.info("{} - Agent has Limits available to Process this Transaction", uuid);

        Transaction transaction = Transaction.builder()
                .transactionKey(uuid)
                .transactionDate(todayDateTime.toLocalDate())
                .transactionTime(todayDateTime.toLocalTime())
                .transactionType(walletCashInRequest.getType())
                .agentAuthorizationDetail(loggedInUserMobileNumber)
                .agentId(agentDetails.getAgentId())
                .amount(walletCashInRequest.getAmount())
                .currencyName(walletCashInRequest.getCurrencyName())
                .memberAgentCommission(transferCommissionDto.getAgentMemberCommission() != null ? transferCommissionDto.getAgentMemberCommission().toString() : "0")
                .agentCommission(transferCommissionDto.getAgentCommission() != null ? transferCommissionDto.getAgentCommission().toString() : "0")
                .superAgentCommission(transferCommissionDto.getAgentBankerCommission() != null ? transferCommissionDto.getAgentBankerCommission().toString() : "0")
                .memberWalletId(transferCommissionDto.getAgentMemberAccountNumber())
                .agentWalletId(transferCommissionDto.getAgentAccountNumber())
                .superAgentWalletId(transferCommissionDto.getAgentBankerAccountNumber())
                .commissionCredit(null).commissionCreditWallet(null).commissionDebit(null).commissionDebitWallet(null)
                .fromAccount(walletCashInRequest.getSender()).toAccount(walletCashInRequest.getReceiver())
                .ledgerCredit(null).ledgerDebit(null).hostResponseCode(null).jsonResponse(null).transactionPerformedBy(agentDetails.getFullName())
                .fromCustomerId(walletCashInRequest.getBankCustomerId())
                .fromAccount(walletCashInRequest.getDebtorBankAccountNumber())
                .toCustomerId("AGENT")
                .toAccount(agentDetails.getPhoneNo() + "")
                .currencyName(agentDetails.getCurrency())
                .build();
        log.info("{} - Transaction Log details are noted", uuid);
        transactionService.saveTransactionLog(transaction);
        log.info("{} - service call to bank start", uuid);
        WalletCashInResponse walletCashInResponse = walletService.walletCashIn(uuid, token, walletCashInRequest);
        log.info("{} - service call to bank successful", uuid);
        transaction.setErrorCode("PROCESSED_OK");
        transactionService.saveTransactionLog(transaction);
        log.info("{} - transaction log saved", uuid);

        return walletCashInResponse;
    }


    @Override
    public WalletCashOutResponse walletCashOut(String uuid, KeycloakAuthenticationToken token, WalletCashOutRequest walletCashOutRequest) {
        log.info("{} - cashDepositToAccount - {}", uuid, walletCashOutRequest);
        LocalDateTime todayDateTime = LocalDateTime.now();
        String loggedInUserMobileNumber = AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token);
        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(loggedInUserMobileNumber);
        log.info("{} - agent found", uuid);
        if (!agentDetails.getAgentPackage().getActive()) {
            throw new ObjectNotFoundException(ApiError.PACKAGE_NOT_ACTIVE);
        }
        log.info("{} - agent is active", uuid);
        if (agentDetails.getAgentType() == AgentType.AGENT_MEMBER) {
            log.info("{} - Agent_Member are not allowed to perform Account based Transactions", uuid);
            throw new BadRequestException(ApiError.PERMISSION_DENIED);
        }

        CustomerDetailsResponse customerDetailsResponse = bankAdapterService.validateCustomer(token, walletCashOutRequest.getBankCustomerId());
        boolean isValidated = notificationService.validateMFACore(customerDetailsResponse.getPhoneNumber(), customerDetailsResponse.getEmail()
                , agentDetails.getAgentId(), walletCashOutRequest.getMfaToken(), "AGENT");

        if (!isValidated) {
            throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
        }


        walletCashOutRequest.setUserId(agentDetails.getAgentId().toString());
        walletCashOutRequest.setUserType(UserType.AGENT.name());

        TransferCommissionDto transferCommissionDto = agentManagementUtils.checkAgentLimitsForTransaction(uuid, "WALLET", walletCashOutRequest.getType(),
                walletCashOutRequest.getCurrencyName(), agentDetails, walletCashOutRequest.getAmount(), walletCashOutRequest.getFee(), todayDateTime);
        log.info("{} - Agent has Limits available to Process this Transaction", uuid);

        Transaction transaction = Transaction.builder().transactionKey(uuid).transactionDate(todayDateTime.toLocalDate())
                .transactionTime(todayDateTime.toLocalTime()).transactionType(walletCashOutRequest.getType()).agentAuthorizationDetail(loggedInUserMobileNumber)
                .agentId(agentDetails.getAgentId()).amount(walletCashOutRequest.getAmount()).currencyName(walletCashOutRequest.getCurrencyName())
                .memberAgentCommission(transferCommissionDto.getAgentMemberCommission() != null ? transferCommissionDto.getAgentMemberCommission().toString() : "0")
                .agentCommission(transferCommissionDto.getAgentCommission() != null ? transferCommissionDto.getAgentCommission().toString() : "0")
                .superAgentCommission(transferCommissionDto.getAgentBankerCommission() != null ? transferCommissionDto.getAgentBankerCommission().toString() : "0")
                .memberWalletId(transferCommissionDto.getAgentMemberAccountNumber()).agentWalletId(transferCommissionDto.getAgentAccountNumber())
                .superAgentWalletId(transferCommissionDto.getAgentBankerAccountNumber())
                .commissionCredit(null).commissionCreditWallet(null).commissionDebit(null).commissionDebitWallet(null)
                .fromAccount(walletCashOutRequest.getSender()).toAccount(walletCashOutRequest.getReceiver())
                .ledgerCredit(null).ledgerDebit(null).hostResponseCode(null).jsonResponse(null).transactionPerformedBy(agentDetails.getFullName())
                .fromCustomerId("AGENT")
                .fromAccount(agentDetails.getPhoneNo() + "")
                .toCustomerId(walletCashOutRequest.getBankCustomerId())
                .toAccount(walletCashOutRequest.getToAccountNumber())
                .currencyName(agentDetails.getCurrency())
                .build();
        log.info("{} - Transaction Log details are noted", uuid);
        transactionService.saveTransactionLog(transaction);

        log.info("{} - service call to bank start", uuid);
        WalletCashOutResponse walletCashOutResponse = walletService.walletCashOut(uuid, token, walletCashOutRequest);
        log.info("{} - service call to bank successful", uuid);
        transaction.setErrorCode("PROCESSED_OK");
        transactionService.saveTransactionLog(transaction);
        log.info("{} - transaction log saved", uuid);

        return walletCashOutResponse;
    }


    @Override
    public WalletToWalletResponse walletToWalletInternal(String uuid, KeycloakAuthenticationToken token, WalletToWalletRequest walletToWalletRequest) {
        log.info("{} - cashDepositToAccount - {}", uuid, walletToWalletRequest);
        LocalDateTime todayDateTime = LocalDateTime.now();
        String loggedInUserMobileNumber = AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token);
        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(loggedInUserMobileNumber);
        log.info("{} - agent found", uuid);
        if (!agentDetails.getAgentPackage().getActive()) {
            throw new ObjectNotFoundException(ApiError.PACKAGE_NOT_ACTIVE);
        }
        log.info("{} - agent is active", uuid);

        // MFA Validation will be done on the basis of Debitor
        String mfaEmail = "";
        String mfaMobile = "";
        String debitorPhone = walletToWalletRequest.getDebtorUserId();
        String creditorPhone = walletToWalletRequest.getCreditorUserId();

        if (walletToWalletRequest.getDebtorUserType().equals(UserType.CUSTOMER.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.CUSTOMER.name())) {
            throw new BadRequestException("CUSTOMER TO CUSTOMER TRANSFER NOT SUPPORTED");
        } else if (walletToWalletRequest.getDebtorUserType().equals(UserType.AGENT.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.AGENT.name())) {
            if (walletToWalletRequest.getDebtorUserId().equals(walletToWalletRequest.getCreditorUserId())) {
                throw new BadRequestException("SAME AGENT TRANSFER NOT SUPPORTED");
            }
        }

        if (walletToWalletRequest.getDebtorUserType().equals(UserType.CUSTOMER.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.AGENT.name())) {

            log.info("{} - CUSTOMER TO AGENT", uuid);

            WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, walletToWalletRequest.getDebtorUserId());
            mfaEmail = walletCustomerDetails.getEmailAddress();
            mfaMobile = agentDetails.getPhoneNo();


            walletToWalletRequest.setDebtorUserId(walletCustomerDetails.getCustomerId().toString());
            walletToWalletRequest.setCreditorUserId(agentDetails.getAgentId().toString());


        } else if (walletToWalletRequest.getDebtorUserType().equals(UserType.AGENT.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.CUSTOMER.name())) {

            log.info("{} - AGENT TO CUSTOMER", uuid);

            WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, walletToWalletRequest.getCreditorUserId());
            mfaEmail = walletCustomerDetails.getEmailAddress();
            mfaMobile = agentDetails.getPhoneNo();


            walletToWalletRequest.setDebtorUserId(agentDetails.getAgentId().toString());
            walletToWalletRequest.setCreditorUserId(walletCustomerDetails.getCustomerId().toString());

        } else if (walletToWalletRequest.getDebtorUserType().equals(UserType.AGENT.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.AGENT.name())) {

            log.info("{} - AGENT TO AGENT", uuid);

            mfaEmail = agentDetails.getAgentEmailAddress();
            mfaMobile = agentDetails.getPhoneNo();


            AgentDetails creditorAgentDetails = agentDetailsService.findActiveAgentsByMobileNumber(walletToWalletRequest.getCreditorUserId());
            walletToWalletRequest.setDebtorUserId(agentDetails.getAgentId() + "");
            walletToWalletRequest.setCreditorUserId(creditorAgentDetails.getAgentId() + "");

        }

        log.info("{} - EMAIL {}", uuid, mfaEmail);
        log.info("{} - MOBILE {}", uuid, mfaMobile);

        // end


//        if (walletToWalletRequest.getDebtorUserType().equals(UserType.AGENT.name())) {
//            walletToWalletRequest.setDebtorUserId(agentDetails.getAgentId().toString());
//        } else if (walletToWalletRequest.getDebtorUserType().equals(UserType.CUSTOMER.name())) {
//            WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, walletToWalletRequest.getDebtorUserId());
//            walletToWalletRequest.setDebtorUserId(walletCustomerDetails.getCustomerId().toString());
//        }
//
//        if (walletToWalletRequest.getCreditorUserType().equals(UserType.AGENT.name())) {
//            walletToWalletRequest.setCreditorUserId(agentDetails.getAgentId().toString());
//        } else if(walletToWalletRequest.getDebtorUserType().equals(UserType.CUSTOMER.name())) {
//            WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, walletToWalletRequest.getCreditorUserId());
//            walletToWalletRequest.setCreditorUserId(walletCustomerDetails.getCustomerId().toString());
//        }
//
//        if(walletToWalletRequest.getDebtorUserType().equals(UserType.AGENT.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.AGENT.name())){
//            AgentDetails creditorAgentDetails = agentDetailsService.findActiveAgentsByMobileNumber(walletToWalletRequest.getCreditorUserId());
//            walletToWalletRequest.setDebtorUserId(agentDetails.getAgentId()+"");
//            walletToWalletRequest.setCreditorUserId(creditorAgentDetails.getAgentId()+"");
//        }

        TransferCommissionDto transferCommissionDto = agentManagementUtils.checkAgentLimitsForTransaction(uuid, "WALLET", walletToWalletRequest.getType(),
                walletToWalletRequest.getCurrencyName(), agentDetails, walletToWalletRequest.getAmount(), walletToWalletRequest.getFee(), todayDateTime);
        log.info("{} - Agent has Limits available to Process this Transaction", uuid);


        Transaction transaction = Transaction.builder().transactionKey(uuid).transactionDate(todayDateTime.toLocalDate())
                .transactionTime(todayDateTime.toLocalTime())
                .transactionType(walletToWalletRequest.getType())
                .agentAuthorizationDetail(loggedInUserMobileNumber)
                .agentId(agentDetails.getAgentId())
                .amount(walletToWalletRequest.getAmount())
                .currencyName(walletToWalletRequest.getCurrencyCode())
                .memberAgentCommission(transferCommissionDto.getAgentMemberCommission() != null ? transferCommissionDto.getAgentMemberCommission().toString() : "0")
                .agentCommission(transferCommissionDto.getAgentCommission() != null ? transferCommissionDto.getAgentCommission().toString() : "0")
                .superAgentCommission(transferCommissionDto.getAgentBankerCommission() != null ? transferCommissionDto.getAgentBankerCommission().toString() : "0")
                .memberWalletId(transferCommissionDto.getAgentMemberAccountNumber()).agentWalletId(transferCommissionDto.getAgentAccountNumber())
                .superAgentWalletId(transferCommissionDto.getAgentBankerAccountNumber())
                .commissionCredit(null).commissionCreditWallet(null).commissionDebit(null).commissionDebitWallet(null)
                .fromAccount(walletToWalletRequest.getSender()).toAccount(walletToWalletRequest.getReceiver())
                .ledgerCredit(null).ledgerDebit(null).hostResponseCode(null).jsonResponse(null).transactionPerformedBy(agentDetails.getFullName())
                .fromCustomerId(walletToWalletRequest.getDebtorUserType())
                .fromAccount(debitorPhone)
                .toCustomerId(walletToWalletRequest.getCreditorUserType())
                .toAccount(creditorPhone)
                .currencyName(agentDetails.getCurrency())
                .build();

        log.info("{} - Transaction Log details are noted", uuid);


//        boolean isValidated  = notificationService.validateMFACore(mfaMobile , mfaEmail
//                ,agentDetails.getAgentId() , walletToWalletRequest.getMfaToken() , "AGENT");
//        if(walletToWalletRequest.getDebtorUserType().equals("AGENT") && walletToWalletRequest.getCreditorUserType().equals("AGENT"))
//        {
//            isValidated = notificationService.validateMFACore(mfaMobile , mfaEmail
//                    ,agentDetails.getAgentId() , walletToWalletRequest.getMfaToken() , "AGENT");
//        }
//        else if (walletToWalletRequest.getDebtorUserType().equals("CUSTOMER") && walletToWalletRequest.getCreditorUserType().equals("AGENT"))
//        {
//            isValidated = notificationService.validateMFACore(mfaMobile , mfaEmail
//                    ,agentDetails.getAgentId() , walletToWalletRequest.getMfaToken() , "CUSTOMER");
//        }


//        if(!isValidated)
//        {
//            throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
//        }
        transactionService.saveTransactionLog(transaction);
        log.info("{} - service call to bank start", uuid);
        log.info("{} - walletToWalletRequest", walletToWalletRequest);
        WalletToWalletResponse walletToWalletResponse = walletService.walletToWallet(uuid, token, walletToWalletRequest);
        log.info("{} - service call to bank successful", uuid);
        transaction.setErrorCode("PROCESSED_OK");
        transactionService.saveTransactionLog(transaction);
        log.info("{} - transaction log saved", uuid);

        return walletToWalletResponse;
    }

    @Override
    public WalletToWalletResponse walletToWallet(String uuid, KeycloakAuthenticationToken token, WalletToWalletRequest walletToWalletRequest) {
        log.info("{} - cashDepositToAccount - {}", uuid, walletToWalletRequest);
        LocalDateTime todayDateTime = LocalDateTime.now();
        String loggedInUserMobileNumber = AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token);
        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(loggedInUserMobileNumber);
        log.info("{} - agent found", uuid);
        if (!agentDetails.getAgentPackage().getActive()) {
            throw new ObjectNotFoundException(ApiError.PACKAGE_NOT_ACTIVE);
        }
        log.info("{} - agent is active", uuid);

        // MFA Validation will be done on the basis of Debitor
        String mfaEmail = "";
        String mfaMobile = "";

        if (walletToWalletRequest.getDebtorUserType().equals(UserType.CUSTOMER.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.CUSTOMER.name())) {
            throw new BadRequestException("CUSTOMER TO CUSTOMER TRANSFER NOT SUPPORTED");
        } else if (walletToWalletRequest.getDebtorUserType().equals(UserType.AGENT.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.AGENT.name())) {
            if (walletToWalletRequest.getDebtorUserId().equals(walletToWalletRequest.getCreditorUserId())) {
                throw new BadRequestException("SAME AGENT TRANSFER NOT SUPPORTED");
            }
        }

        if (walletToWalletRequest.getDebtorUserType().equals(UserType.CUSTOMER.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.AGENT.name())) {

            log.info("{} - CUSTOMER TO AGENT", uuid);

            WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, walletToWalletRequest.getDebtorUserId());
            mfaEmail = walletCustomerDetails.getEmailAddress();
            mfaMobile = agentDetails.getPhoneNo();


            walletToWalletRequest.setDebtorUserId(walletCustomerDetails.getCustomerId().toString());
            walletToWalletRequest.setCreditorUserId(agentDetails.getAgentId().toString());


        } else if (walletToWalletRequest.getDebtorUserType().equals(UserType.AGENT.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.CUSTOMER.name())) {

            log.info("{} - AGENT TO CUSTOMER", uuid);

            WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, walletToWalletRequest.getCreditorUserId());
            mfaEmail = agentDetails.getAgentEmailAddress();
            mfaMobile = agentDetails.getPhoneNo();


            walletToWalletRequest.setDebtorUserId(agentDetails.getAgentId().toString());
            walletToWalletRequest.setCreditorUserId(walletCustomerDetails.getCustomerId().toString());

        } else if (walletToWalletRequest.getDebtorUserType().equals(UserType.AGENT.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.AGENT.name())) {

            log.info("{} - AGENT TO AGENT", uuid);

            mfaEmail = agentDetails.getAgentEmailAddress();
            mfaMobile = agentDetails.getPhoneNo();


            AgentDetails creditorAgentDetails = agentDetailsService.findActiveAgentsByMobileNumber(walletToWalletRequest.getCreditorUserId());
            walletToWalletRequest.setDebtorUserId(agentDetails.getAgentId() + "");
            walletToWalletRequest.setCreditorUserId(creditorAgentDetails.getAgentId() + "");

        }

        log.info("{} - EMAIL {}", uuid, mfaEmail);
        log.info("{} - MOBILE {}", uuid, mfaMobile);

        // end


//        if (walletToWalletRequest.getDebtorUserType().equals(UserType.AGENT.name())) {
//            walletToWalletRequest.setDebtorUserId(agentDetails.getAgentId().toString());
//        } else if (walletToWalletRequest.getDebtorUserType().equals(UserType.CUSTOMER.name())) {
//            WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, walletToWalletRequest.getDebtorUserId());
//            walletToWalletRequest.setDebtorUserId(walletCustomerDetails.getCustomerId().toString());
//        }
//
//        if (walletToWalletRequest.getCreditorUserType().equals(UserType.AGENT.name())) {
//            walletToWalletRequest.setCreditorUserId(agentDetails.getAgentId().toString());
//        } else if(walletToWalletRequest.getDebtorUserType().equals(UserType.CUSTOMER.name())) {
//            WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, walletToWalletRequest.getCreditorUserId());
//            walletToWalletRequest.setCreditorUserId(walletCustomerDetails.getCustomerId().toString());
//        }
//
//        if(walletToWalletRequest.getDebtorUserType().equals(UserType.AGENT.name()) && walletToWalletRequest.getCreditorUserType().equals(UserType.AGENT.name())){
//            AgentDetails creditorAgentDetails = agentDetailsService.findActiveAgentsByMobileNumber(walletToWalletRequest.getCreditorUserId());
//            walletToWalletRequest.setDebtorUserId(agentDetails.getAgentId()+"");
//            walletToWalletRequest.setCreditorUserId(creditorAgentDetails.getAgentId()+"");
//        }

        TransferCommissionDto transferCommissionDto = agentManagementUtils.checkAgentLimitsForTransaction(uuid, "WALLET", walletToWalletRequest.getType(),
                walletToWalletRequest.getCurrencyName(), agentDetails, walletToWalletRequest.getAmount(), walletToWalletRequest.getFee(), todayDateTime);
        log.info("{} - Agent has Limits available to Process this Transaction", uuid);


        Transaction transaction = Transaction.builder().transactionKey(uuid).transactionDate(todayDateTime.toLocalDate())
                .transactionTime(todayDateTime.toLocalTime())
                .transactionType(walletToWalletRequest.getType())
                .agentAuthorizationDetail(loggedInUserMobileNumber)
                .agentId(agentDetails.getAgentId())
                .amount(walletToWalletRequest.getAmount())
                .currencyName(walletToWalletRequest.getCurrencyCode())
                .memberAgentCommission(transferCommissionDto.getAgentMemberCommission() != null ? transferCommissionDto.getAgentMemberCommission().toString() : "0")
                .agentCommission(transferCommissionDto.getAgentCommission() != null ? transferCommissionDto.getAgentCommission().toString() : "0")
                .superAgentCommission(transferCommissionDto.getAgentBankerCommission() != null ? transferCommissionDto.getAgentBankerCommission().toString() : "0")
                .memberWalletId(transferCommissionDto.getAgentMemberAccountNumber()).agentWalletId(transferCommissionDto.getAgentAccountNumber())
                .superAgentWalletId(transferCommissionDto.getAgentBankerAccountNumber())
                .commissionCredit(null).commissionCreditWallet(null).commissionDebit(null).commissionDebitWallet(null)
                .fromAccount(walletToWalletRequest.getSender()).toAccount(walletToWalletRequest.getReceiver())
                .ledgerCredit(null).ledgerDebit(null).hostResponseCode(null).jsonResponse(null).transactionPerformedBy(agentDetails.getFullName())
                .fromCustomerId(walletToWalletRequest.getDebtorUserType())
                .fromAccount(walletToWalletRequest.getDebtorUserId())
                .toCustomerId(walletToWalletRequest.getCreditorUserType())
                .toAccount(walletToWalletRequest.getCreditorUserId())
                .currencyName(agentDetails.getCurrency())
                .build();
        log.info("{} - Transaction Log details are noted", uuid);


        boolean isValidated = notificationService.validateMFACore(mfaMobile, mfaEmail
                , agentDetails.getAgentId(), walletToWalletRequest.getMfaToken(), "AGENT");
//        if(walletToWalletRequest.getDebtorUserType().equals("AGENT") && walletToWalletRequest.getCreditorUserType().equals("AGENT"))
//        {
//            isValidated = notificationService.validateMFACore(mfaMobile , mfaEmail
//                    ,agentDetails.getAgentId() , walletToWalletRequest.getMfaToken() , "AGENT");
//        }
//        else if (walletToWalletRequest.getDebtorUserType().equals("CUSTOMER") && walletToWalletRequest.getCreditorUserType().equals("AGENT"))
//        {
//            isValidated = notificationService.validateMFACore(mfaMobile , mfaEmail
//                    ,agentDetails.getAgentId() , walletToWalletRequest.getMfaToken() , "CUSTOMER");
//        }


        if (!isValidated) {
            throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
        }

        log.info("{} - service call to bank start", uuid);
        log.info("{} - walletToWalletRequest", walletToWalletRequest);

        transactionService.saveTransactionLog(transaction);

        WalletToWalletResponse walletToWalletResponse = walletService.walletToWallet(uuid, token, walletToWalletRequest);
        log.info("{} - service call to bank successful", uuid);
        transaction.setErrorCode("PROCESSED_OK");
        transactionService.saveTransactionLog(transaction);
        log.info("{} - transaction log saved", uuid);

        return walletToWalletResponse;
    }

    @Override
    public List<CustomerBalanceResponse> getAgentWalletBalance(KeycloakAuthenticationToken token) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
//        if (agentDetails.getStatus() != AgentStatus.ACTIVE) {
//            throw new AccountNotActiveException(ApiError.AGENT_NOT_ACTIVE);
//        }
        return walletService.walletBalance(token, UserType.AGENT.toString(), agentDetails.getAgentId().toString());

    }

    @Override
    public WalletCustomerDetails validateCustomer(String uuid, KeycloakAuthenticationToken token, ValidateCustomerRequest validateCustomerRequest) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));

        WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, validateCustomerRequest.getPhoneNumber());

        if (validateCustomerRequest.getBankCustomerId() != null) {
            if (!walletCustomerDetails.getBankCustomerId().equals(validateCustomerRequest.getBankCustomerId())) {
                throw new BadRequestException(ApiError.INVALID_CUSTOMER);
            }
        }
        if (!walletCustomerDetails.getIdDocumentNumber().equals(validateCustomerRequest.getIdDocumentNumber())) {
            throw new BadRequestException(ApiError.INVALID_CUSTOMER);
        }

        return walletCustomerDetails;

    }

    @Override
    public List<CustomerBalanceResponse> walletBalanceByLinkedAccountNumber(KeycloakAuthenticationToken token) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
//        if (agentDetails.getStatus() != AgentStatus.ACTIVE) {
//            throw new AccountNotActiveException(ApiError.AGENT_NOT_ACTIVE);
//        }
//        if (agentDetails.getAgentType() != AgentType.AGENT_BANKER) {
//            log.info("Only Agent_Banker allowed to perform Account based Transactions");
//            throw new BadRequestException(ApiError.PERMISSION_DENIED);
//        }
        String accountNumber = agentDetails.getLinkedAccountNumber();
        return walletService.getAccountBalanceByLinkedAccountNumber(token, accountNumber);
    }

    @Override
    public CustomerBalanceResponse walletUsernameByPhoneNumber(KeycloakAuthenticationToken token) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
        if (agentDetails.getStatus() != AgentStatus.ACTIVE) {
            throw new AccountNotActiveException(ApiError.AGENT_NOT_ACTIVE);
        }
        if (agentDetails.getAgentType() != AgentType.AGENT_BANKER) {
            log.info("Only Agent_Banker allowed to perform Account based Transactions");
            throw new BadRequestException(ApiError.PERMISSION_DENIED);
        }
        String phoneNumber = agentDetails.getPhoneNo();
        return walletService.getWalletUsernameByPhoneNumber(token, phoneNumber);
    }

    @Override
    public SendMoneyToNonWalletResponse sendMoneyToNonWallet(String uuid, KeycloakAuthenticationToken token, SendMoneyToNonWalletRequest sendMoneyToNonWalletRequest) {
        return walletService.sendMoneyToNonWallet(token, sendMoneyToNonWalletRequest);
    }

    @Override
    public ProcessPaymentDetailsResponse processPaymentDetails(String uuid, KeycloakAuthenticationToken token, ProcessPaymentDetailsRequest processPaymentDetailsRequest) {
        PaymentDetailsResponse paymentDetailsResponse = walletService.paymentDetails(token, processPaymentDetailsRequest);
        return walletService.processPaymentDetails(token, processPaymentDetailsRequest);
    }
}
