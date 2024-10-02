package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.*;
import com.digibank.agentmanagement.domain.accounttransfer.AccountOpeningDocument;
import com.digibank.agentmanagement.domain.accounttransfer.PersonAccount;
import com.digibank.agentmanagement.domain.packagemanagement.*;
import com.digibank.agentmanagement.exception.*;
import com.digibank.agentmanagement.repository.accounttransfer.PersonAccountRepository;
import com.digibank.agentmanagement.service.*;
import com.digibank.agentmanagement.service.accounttransfer.AccountOpeningDocumentService;
import com.digibank.agentmanagement.service.packagemanagement.*;
import com.digibank.agentmanagement.utils.AgentManagementPropertyHolder;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.TransferCommissionDto;
import com.digibank.agentmanagement.web.dto.accounttransfer.request.*;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.*;
import com.digibank.agentmanagement.utils.ApiError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.hibernate.usertype.UserType;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@AllArgsConstructor
public class AccountTransactionServiceImpl implements AccountTransactionService {

    private final AgentManagementPropertyHolder agentManagementPropertyHolder;

    private final AgentManagementUtils agentManagementUtils;
    private final AgentDetailsService agentDetailsService;
    private final OperationService operationService;
    private final CurrencyService currencyService;
    private final AgentLimitProfileService agentLimitProfileService;
    private final PackageOperationPermissionService packageOperationPermissionService;
    private final PackageCurrencyLimitService packageCurrencyLimitService;
    private final TransactionService transactionService;
    private final AssetService assetService;
    private final PersonAccountRepository personAccountRepository;
    private final AccountOpeningDocumentService accountOpeningDocumentService;

    private final BankAdapterService bankAdapterService;
    private final NotificationService notificationService;

    @Override
    public AccountBalanceResponse getAccountBalance(KeycloakAuthenticationToken token, BalanceInquiryRequest balanceInquiryRequest) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
        if (agentDetails.getStatus() != AgentStatus.ACTIVE) {
            throw new AccountNotActiveException(ApiError.AGENT_NOT_ACTIVE);
        }
        if (agentDetails.getAgentType() != AgentType.AGENT_BANKER) {
            log.info("Only Agent_Banker allowed to perform Account based Transactions");
            throw new BadRequestException(ApiError.PERMISSION_DENIED);
        }

        CustomerDetailsResponse customerDetailsResponse = bankAdapterService.validateCustomer(token , balanceInquiryRequest.getCustomerId());
        boolean isValidated = notificationService.validateMFACore(customerDetailsResponse.getPhoneNumber() , customerDetailsResponse.getEmail()
        ,agentDetails.getAgentId() , balanceInquiryRequest.getMfaToken() , "AGENT");

        if(!isValidated){
            throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
        }

        AccountBalanceResponse accountBalanceResponse = bankAdapterService.getAccountBalance(token, balanceInquiryRequest.getAccountNumber());

        if(balanceInquiryRequest.getSendType().equals("SMS")){
            notificationService.sendSMSNotification(agentDetails , MFAChannel.SMS ,
                    "BANK_AC_BALANCE" , "BANK_AC_BALANCE" ,customerDetailsResponse.getPhoneNumber() , customerDetailsResponse.getCustomerId(), Map.of("bankAccount", accountBalanceResponse.getAccountNumber() , "amount" , accountBalanceResponse.getBalance().toString() ),
                    false , false , true);
        }else if(balanceInquiryRequest.getSendType().equals("EMAIL")){
            notificationService.sendEmailNotification(agentDetails , MFAChannel.EMAIL ,
                    "BANK_AC_BALANCE" , "BANK_AC_BALANCE" ,customerDetailsResponse.getEmail() , customerDetailsResponse.getCustomerId(), Map.of("bankAccount", accountBalanceResponse.getAccountNumber() , "amount" , accountBalanceResponse.getBalance().toString() ),
                    false , true , false);
        }
        else if(balanceInquiryRequest.getSendType().equals("BOTH")){

            notificationService.sendSMSNotification(agentDetails , MFAChannel.SMS ,
                    "BANK_AC_BALANCE" , "BANK_AC_BALANCE" ,customerDetailsResponse.getPhoneNumber() , customerDetailsResponse.getCustomerId(), Map.of("bankAccount", accountBalanceResponse.getAccountNumber() , "amount" , accountBalanceResponse.getBalance().toString() ),
                    false , false , true);

            notificationService.sendEmailNotification(agentDetails , MFAChannel.EMAIL ,
                    "BANK_AC_BALANCE" , "BANK_AC_BALANCE" ,customerDetailsResponse.getEmail() ,customerDetailsResponse.getCustomerId(), Map.of("bankAccount", accountBalanceResponse.getAccountNumber() , "amount" , accountBalanceResponse.getBalance().toString() ),
                    false , true , false);

        }

        return accountBalanceResponse;
    }

    @Override
    public List<AccountStatementResponse> getAccountStatement(KeycloakAuthenticationToken token, AccountStatementRequest accountStatementRequest) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
        if (agentDetails.getStatus() != AgentStatus.ACTIVE) {
            throw new AccountNotActiveException(ApiError.AGENT_NOT_ACTIVE);
        }
        if (agentDetails.getAgentType() != AgentType.AGENT_BANKER) {
            log.info("Only Agent_Banker allowed to perform Account based Transactions");
            throw new BadRequestException(ApiError.PERMISSION_DENIED);
        }

        CustomerDetailsResponse customerDetailsResponse = bankAdapterService.validateCustomer(token , accountStatementRequest.getCustomerId());
        boolean isValidated = notificationService.validateMFACore(customerDetailsResponse.getPhoneNumber() , customerDetailsResponse.getEmail()
                ,agentDetails.getAgentId() , accountStatementRequest.getMfaToken() , "AGENT");

        if(!isValidated){
            throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
        }
        List<AccountStatementResponse> accountStatementResponseList =  bankAdapterService.accountStatementRequest(token, accountStatementRequest.getAccountNumber(), accountStatementRequest.getFromDate(), accountStatementRequest.getToDate(), Integer.parseInt(accountStatementRequest.getPageNumber()) , Integer.parseInt(accountStatementRequest.getSize()));

        if(accountStatementRequest.getSendViaEmail() == true && accountStatementResponseList.size() != 0){

            AtomicReference<String> htmlTemplate = new AtomicReference<>("<table style=\"width:100%,border:1px solid black\"><tr style=\"border:1px solid black\"><th style=\"border:1px solid black\">Date</th><th style=\"border:1px solid black\">Type</th><th style=\"border:1px solid black\">Amount</th><th style=\"border:1px solid black\">Reference</th></tr><tr style=\"border:1px solid black\">");

            accountStatementResponseList.forEach(accountStatementResponse -> {

                htmlTemplate.set(htmlTemplate + "<td style=\"border:1px solid black\">" + accountStatementResponse.getTranDate().split("T")[0] + "</td>");
                htmlTemplate.set(htmlTemplate + "<td style=\"border:1px solid black\">" + accountStatementResponse.getTitle() + "</td>");
                htmlTemplate.set(htmlTemplate + "<td style=\"border:1px solid black\">" + accountStatementResponse.getAmt() + "</td>");
                htmlTemplate.set(htmlTemplate + "<td style=\"border:1px solid black\">" + accountStatementResponse.getOpe() + "</td>");
                htmlTemplate.set(htmlTemplate + "</tr>");

            });

            htmlTemplate.set(htmlTemplate + "</table>");
            notificationService.sendEmailNotification(agentDetails , MFAChannel.EMAIL , "BANK_AC_STATEMENT" , "BANK_AC_STATEMENT" , customerDetailsResponse.getEmail() , customerDetailsResponse.getCustomerId(),
                    Map.of("bankAccount", accountStatementRequest.getAccountNumber() , "startdate" ,  accountStatementRequest.getFromDate().toString() , "enddate" , accountStatementRequest.getToDate().toString() , "statement" , htmlTemplate.get() ),false,true,false);
        }


        return accountStatementResponseList;
    }

    @Override
    @Transactional
    public CashDepositWithdrawResponse cashDepositToAccount(String uuid, KeycloakAuthenticationToken token, CashDepositRequest cashDepositRequest) {
        log.info("{} - cashDepositToAccount - {}", uuid, cashDepositRequest);
        LocalDateTime todayDateTime = LocalDateTime.now();
        String loggedInUserMobileNumber = AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token);
        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(loggedInUserMobileNumber);
        log.info("{} - agent found", uuid);
        if (!agentDetails.getAgentPackage().getActive()) {
            throw new ObjectNotFoundException(ApiError.PACKAGE_NOT_ACTIVE);
        }
        log.info("{} - agent is active", uuid);
        if (agentDetails.getAgentType() != AgentType.AGENT_BANKER) {
            log.info("{} - Only Agent_Banker allowed to perform Account based Transactions", uuid);
            throw new BadRequestException(ApiError.PERMISSION_DENIED);
        }

        cashDepositRequest.setReceiver(cashDepositRequest.getCustomerBankAccount());
        cashDepositRequest.setSender(agentDetails.getLinkedAccountNumber());

        TransferCommissionDto transferCommissionDto = agentManagementUtils.checkAgentLimitsForTransaction(uuid, "ACCOUNT", cashDepositRequest.getType(),
                cashDepositRequest.getCurrencyName(), agentDetails, cashDepositRequest.getAmount(), cashDepositRequest.getFee(), todayDateTime);
        log.info("{} - Agent has Limits available to Process this Transaction", uuid);

        Transaction transaction = Transaction.builder().transactionKey(uuid).transactionDate(todayDateTime.toLocalDate())
                .transactionTime(todayDateTime.toLocalTime()).transactionType(cashDepositRequest.getType()).agentAuthorizationDetail(loggedInUserMobileNumber)
                .agentId(agentDetails.getAgentId()).amount(cashDepositRequest.getAmount()).currencyName(cashDepositRequest.getCurrencyName())
                .memberAgentCommission(transferCommissionDto.getAgentMemberCommission().toString()).agentCommission(transferCommissionDto.getAgentCommission().toString())
                .superAgentCommission(transferCommissionDto.getAgentBankerCommission().toString())
                .memberWalletId(transferCommissionDto.getAgentMemberAccountNumber()).agentWalletId(transferCommissionDto.getAgentAccountNumber())
                .superAgentWalletId(transferCommissionDto.getAgentBankerAccountNumber())
                .commissionCredit(null).commissionCreditWallet(null).commissionDebit(null).commissionDebitWallet(null)
                .fromAccount(cashDepositRequest.getSender()).toAccount(cashDepositRequest.getReceiver())
                .ledgerCredit(null).ledgerDebit(null).hostResponseCode(null).jsonResponse(null).transactionPerformedBy(agentDetails.getFullName()).build();
        log.info("{} - Transaction Log details are noted", uuid);

        log.info("{} - service call to bank start", uuid);
        CashDepositWithdrawResponse cashDepositWithdrawResponse = bankAdapterService.depositCash(uuid, token, cashDepositRequest);
        log.info("{} - service call to bank successful", uuid);
        transactionService.saveTransactionLog(transaction);
        log.info("{} - transaction log saved", uuid);

        return cashDepositWithdrawResponse;
    }

    @Override
    @Transactional
    public CashDepositWithdrawResponse cashWithdrawalFromAccount(String uuid, KeycloakAuthenticationToken token, CashWithDrawlRequest cashWithDrawlRequest) {
        LocalDateTime todayDateTime = LocalDateTime.now();
        String loggedInUserMobileNumber = AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token);
        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(loggedInUserMobileNumber);
        if (!agentDetails.getAgentPackage().getActive()) {
            throw new ObjectNotFoundException(ApiError.PACKAGE_NOT_ACTIVE);
        }

        if (agentDetails.getAgentType() != AgentType.AGENT_BANKER) {
            log.info("{} - Only Agent_Banker allowed to perform Account based Transactions", uuid);
            throw new BadRequestException(ApiError.PERMISSION_DENIED);
        }

        cashWithDrawlRequest.setSender(cashWithDrawlRequest.getCustomerBankAccount());
        cashWithDrawlRequest.setReceiver(agentDetails.getLinkedAccountNumber());

        TransferCommissionDto transferCommissionDto = agentManagementUtils.checkAgentLimitsForTransaction(uuid, "ACCOUNT", cashWithDrawlRequest.getType(),
                cashWithDrawlRequest.getCurrencyName(), agentDetails, cashWithDrawlRequest.getAmount(), cashWithDrawlRequest.getFee(), todayDateTime);
        log.info("{} - Agent has Limits available to Process this Transaction", uuid);

        Transaction transaction = Transaction.builder().transactionKey(uuid).transactionDate(todayDateTime.toLocalDate())
                .transactionTime(todayDateTime.toLocalTime()).transactionType(cashWithDrawlRequest.getType()).agentAuthorizationDetail(loggedInUserMobileNumber)
                .agentId(agentDetails.getAgentId()).amount(cashWithDrawlRequest.getAmount()).currencyName(cashWithDrawlRequest.getCurrencyName())
                .memberAgentCommission(transferCommissionDto.getAgentMemberCommission().toString()).agentCommission(transferCommissionDto.getAgentCommission().toString())
                .superAgentCommission(transferCommissionDto.getAgentBankerCommission().toString())
                .memberWalletId(transferCommissionDto.getAgentMemberAccountNumber()).agentWalletId(transferCommissionDto.getAgentAccountNumber())
                .superAgentWalletId(transferCommissionDto.getAgentBankerAccountNumber())
                .commissionCredit(null).commissionCreditWallet(null).commissionDebit(null).commissionDebitWallet(null)
                .fromAccount(cashWithDrawlRequest.getSender()).toAccount(cashWithDrawlRequest.getReceiver())
                .ledgerCredit(null).ledgerDebit(null).hostResponseCode(null).jsonResponse(null).transactionPerformedBy(agentDetails.getFullName()).build();
        log.info("{} - Transaction Log details are noted", uuid);

        log.info("{} - service call to bank start", uuid);
        CashDepositWithdrawResponse cashDepositWithdrawResponse = bankAdapterService.withdrawlCash(uuid, token, cashWithDrawlRequest);
        log.info("{} - service call to bank successful", uuid);
        transactionService.saveTransactionLog(transaction);
        log.info("{} - transaction log saved", uuid);
        return cashDepositWithdrawResponse;
    }

    @Override
    public CustomerDetailsResponse validateCustomer(KeycloakAuthenticationToken token, String customerId) {
        return bankAdapterService.validateCustomer(token, customerId);
    }

    @Override
    public List<CustomerAccountsResponse> customerAccounts(KeycloakAuthenticationToken token, String customerId) {
        return bankAdapterService.customerAccounts(token, customerId);
    }


    @Override
    public CustomerAccountsResponse getAccountDetails(KeycloakAuthenticationToken token, String accountNumber){
        return bankAdapterService.accountDetails(token, accountNumber);
    }

    @Override
    public AccountBalanceResponse getAgentAccountBalance(KeycloakAuthenticationToken token ,String accountNumber) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
//        if (agentDetails.getStatus() != AgentStatus.ACTIVE) {
//            throw new AccountNotActiveException(ApiError.AGENT_NOT_ACTIVE);
//        }
//        if (agentDetails.getAgentType() != AgentType.AGENT_BANKER) {
//            log.info("Only Agent_Banker allowed to perform Account based Transactions");
//            throw new BadRequestException(ApiError.PERMISSION_DENIED);
//        }
        //String accountNumber = agentDetails.getLinkedAccountNumber();
        return bankAdapterService.getAccountBalance(token, accountNumber);
    }

    @Override
    public List<AccountStatementResponse> getAgentAccountStatement(KeycloakAuthenticationToken token, String fromDate,
                                                              String toDate, Integer pageNumber, Integer size , String accountNumber) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
//        if (agentDetails.getStatus() != AgentStatus.ACTIVE) {
//            throw new AccountNotActiveException(ApiError.AGENT_NOT_ACTIVE);
//        }
//        if (agentDetails.getAgentType() != AgentType.AGENT_BANKER) {
//            log.info("Only Agent_Banker allowed to perform Account based Transactions");
//            throw new BadRequestException(ApiError.PERMISSION_DENIED);
//        }
        //String accountNumber = agentDetails.getLinkedAccountNumber();
        return bankAdapterService.accountStatementRequest(token, accountNumber, fromDate, toDate, pageNumber, size);
    }

    @Override
    @Transactional
    public AccountOpenResponse openAccount(String uuid, KeycloakAuthenticationToken token, AccountOpenRequest accountOpenRequest){
        log.info("{} - openAccount - {}", uuid, accountOpenRequest);
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
        agentManagementUtils.checkAgentValidity(agentDetails);

        PersonAccount personAccount = new PersonAccount();
        BeanUtils.copyProperties(accountOpenRequest, personAccount);
        personAccountRepository.save(personAccount);
        if(personAccount.getAccountId() != 0) {
            if(accountOpenRequest.getSelfiePhoto() != null) {
                String fileName = agentManagementUtils.createFileAtPath(agentManagementPropertyHolder.getFileStoreBasePath(), accountOpenRequest.getSelfiePhoto());
                AccountOpeningDocument accountOpeningDocument = createAccountOpeningDocument(IDDocumentType.SELFIE_DOCUMENT,
                        accountOpenRequest.getSelfiePhoto().getOriginalFilename(),
                        fileName, agentDetails, personAccount);
                accountOpeningDocumentService.add(accountOpeningDocument);
            }
            if(CollectionUtils.isNotEmpty(accountOpenRequest.getIdentityDocuments())){
                for (MultipartFile idDocument: accountOpenRequest.getIdentityDocuments()) {
                    String fileName = agentManagementUtils.createFileAtPath(agentManagementPropertyHolder.getFileStoreBasePath(), idDocument);
                    AccountOpeningDocument accountOpeningDocument = createAccountOpeningDocument(IDDocumentType.ID_DOCUMENT,
                            idDocument.getOriginalFilename(),
                            fileName, agentDetails, personAccount);
                    accountOpeningDocumentService.add(accountOpeningDocument);
                }
            }
        }
        return AccountOpenResponse.builder().code("0").description("Success").details("New account information is saved successfully").build();
    }

    private AccountOpeningDocument createAccountOpeningDocument(IDDocumentType selfieDocument, String originalFilename,
                                                                String fileName, AgentDetails agentDetails,
                                                                PersonAccount personAccount) {
        AccountOpeningDocument accountOpeningDocument = new AccountOpeningDocument();
        accountOpeningDocument.setDocumentType(selfieDocument);
        accountOpeningDocument.setDocumentFileNameOrignal(originalFilename);
        accountOpeningDocument.setDocumentFileName(fileName);
        accountOpeningDocument.setLinkedModule(LinkedModule.ACCOUNT);
        accountOpeningDocument.setAgentDetails(agentDetails);
        accountOpeningDocument.setPersonAccount(personAccount);
        return accountOpeningDocument;
    }
}
