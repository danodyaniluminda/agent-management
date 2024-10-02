package com.digibank.agentmanagement.service.accounttransfer.impl;

import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.AgentStatus;
import com.digibank.agentmanagement.domain.AgentType;
import com.digibank.agentmanagement.domain.Transaction;
import com.digibank.agentmanagement.exception.AccountNotActiveException;
import com.digibank.agentmanagement.exception.BadRequestException;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.service.AgentDetailsService;
import com.digibank.agentmanagement.service.BankAdapterService;
import com.digibank.agentmanagement.service.NotificationService;
import com.digibank.agentmanagement.service.TransactionService;
import com.digibank.agentmanagement.service.accounttransfer.AccountPaymentService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.dto.TransferCommissionDto;
import com.digibank.agentmanagement.web.dto.accounttransfer.request.AccountBillPayRequest;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.AccountBillPayResponse;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.CustomerDetailsResponse;
import com.digibank.agentmanagement.web.dto.common.Bill;
import com.digibank.agentmanagement.web.dto.common.PaymentMerchant;
import com.digibank.agentmanagement.web.dto.common.PaymentService;
import com.digibank.agentmanagement.web.dto.common.ServiceOption;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AccountPaymentServiceImpl implements AccountPaymentService {

    private final TransactionService transactionService;
    private final AgentDetailsService agentDetailsService;
    private final AgentManagementUtils agentManagementUtils;
    private final BankAdapterService bankAdapterService;
    private final NotificationService notificationService;

    @Override
    public List<PaymentService> getServices(KeycloakAuthenticationToken token) {
        agentManagementUtils.checkAgentValidity(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return transactionService.getServices(token);
    }

    @Override
    public List<PaymentMerchant> getMerchants(KeycloakAuthenticationToken token) {
        agentManagementUtils.checkAgentValidity(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return transactionService.getMerchants(token);
    }

    @Override
    public List<Bill> getBills(KeycloakAuthenticationToken token, String merchantCode, String serviceNumber, String serviceId) {
        agentManagementUtils.checkAgentValidity(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return transactionService.getBills(token, merchantCode, serviceNumber, serviceId);
    }

    @Override
    public List<ServiceOption> getVouchers(KeycloakAuthenticationToken token, String serviceId) {
        agentManagementUtils.checkAgentValidity(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return transactionService.getVouchers(token, serviceId);
    }

    @Override
    public List<ServiceOption> getProducts(KeycloakAuthenticationToken token, String serviceId) {
        agentManagementUtils.checkAgentValidity(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return transactionService.getProducts(token, serviceId);
    }

    @Override
    public List<ServiceOption> getSubscriptions(KeycloakAuthenticationToken token, String merchantCode, String serviceNumber, String serviceId) {
        agentManagementUtils.checkAgentValidity(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return transactionService.getSubscriptions(token, merchantCode, serviceNumber, serviceId);
    }

    @Override
    @Transactional
    public AccountBillPayResponse payBill(KeycloakAuthenticationToken token, String uuid, AccountBillPayRequest accountBillPayRequest) {
        log.info("{} - Processing Request: {}", uuid, accountBillPayRequest);
        LocalDateTime todayDateTime = LocalDateTime.now();
        String loggedInUserMobileNumber = AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token);
        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(loggedInUserMobileNumber);
        log.info("{} - agent found", uuid);
//        agentManagementUtils.checkAgentRightsForTransaction(agentDetails, AgentType.AGENT_BANKER);
        if (!agentDetails.getAgentPackage().getActive()) {
            throw new ObjectNotFoundException(ApiError.PACKAGE_NOT_ACTIVE);
        }
        TransferCommissionDto transferCommissionDto = agentManagementUtils.checkAgentLimitsForTransaction(uuid, "ACCOUNT", accountBillPayRequest.getType(),
                accountBillPayRequest.getCurrencyName(), agentDetails, accountBillPayRequest.getAmount(), accountBillPayRequest.getFee(), todayDateTime);
        log.info("{} - Agent has Limits available to Process this Transaction", uuid);

        Transaction transaction = Transaction.builder().transactionKey(uuid).transactionDate(todayDateTime.toLocalDate())
                .transactionTime(todayDateTime.toLocalTime()).transactionType(accountBillPayRequest.getType()).agentAuthorizationDetail(loggedInUserMobileNumber)
                .agentId(agentDetails.getAgentId()).amount(accountBillPayRequest.getAmount()).currencyName(accountBillPayRequest.getCurrencyName())
                .memberAgentCommission(transferCommissionDto.getAgentMemberCommission() != null ? transferCommissionDto.getAgentMemberCommission().toString() : "0")
                .agentCommission(transferCommissionDto.getAgentCommission() != null ? transferCommissionDto.getAgentCommission().toString() : "0")
                .superAgentCommission(transferCommissionDto.getAgentBankerCommission() != null ? transferCommissionDto.getAgentBankerCommission().toString() : "0")
                .memberWalletId(transferCommissionDto.getAgentMemberAccountNumber()).agentWalletId(transferCommissionDto.getAgentAccountNumber())
                .superAgentWalletId(transferCommissionDto.getAgentBankerAccountNumber())
                .commissionCredit(null).commissionCreditWallet(null).commissionDebit(null).commissionDebitWallet(null)
                .ledgerCredit(null).ledgerDebit(null).hostResponseCode(null).jsonResponse(null).transactionPerformedBy(agentDetails.getFullName()).build();
        log.info("{} - Transaction Log details are noted", uuid);

        log.info("{} - service call to bank start", uuid);
		CustomerDetailsResponse customerDetailsResponse = bankAdapterService.validateCustomer(token , accountBillPayRequest.getCustomerId());
        boolean isValidated = notificationService.validateMFACore(customerDetailsResponse.getPhoneNumber() , customerDetailsResponse.getEmail()
                ,agentDetails.getAgentId() , accountBillPayRequest.getMfaToken() , "AGENT");

        if(!isValidated){
            throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
        }
        AccountBillPayResponse accountBillPayResponse = transactionService.payBill(token, uuid, accountBillPayRequest);
        log.info("{} - service call to bank end successful", uuid);
        transactionService.saveTransactionLog(transaction);
        log.info("{} - transaction log saved", uuid);
        return accountBillPayResponse;
    }
}
