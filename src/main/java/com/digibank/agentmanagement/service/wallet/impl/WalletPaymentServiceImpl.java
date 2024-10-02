package com.digibank.agentmanagement.service.wallet.impl;

import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.AgentType;
import com.digibank.agentmanagement.domain.Transaction;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.service.AgentDetailsService;
import com.digibank.agentmanagement.service.TransactionService;
import com.digibank.agentmanagement.service.WalletService;
import com.digibank.agentmanagement.service.wallet.WalletPaymentService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.dto.TransferCommissionDto;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.AccountBillPayResponse;
import com.digibank.agentmanagement.web.dto.common.Bill;
import com.digibank.agentmanagement.web.dto.common.PaymentMerchant;
import com.digibank.agentmanagement.web.dto.common.PaymentService;
import com.digibank.agentmanagement.web.dto.common.ServiceOption;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.PayBillRequest;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.PayBillResponse;
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
public class WalletPaymentServiceImpl implements WalletPaymentService {

    private final WalletService walletService;
    private final AgentDetailsService agentDetailsService;
    private final AgentManagementUtils agentManagementUtils;
    private final TransactionService transactionService;

    @Override
    public List<PaymentService> getServices(KeycloakAuthenticationToken token) {
        agentManagementUtils.checkAgentActive(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return walletService.getServices(token);
    }

    @Override
    public List<PaymentMerchant> getMerchants(KeycloakAuthenticationToken token) {
        agentManagementUtils.checkAgentActive(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return walletService.getMerchants(token);
    }

    @Override
    public List<Bill> getBills(KeycloakAuthenticationToken token, String merchant, String serviceNumber, String serviceId) {
        agentManagementUtils.checkAgentActive(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return walletService.getBills(token, merchant, serviceNumber, serviceId);
    }

    @Override
    public List<ServiceOption> getVouchers(KeycloakAuthenticationToken token, String serviceId) {
        agentManagementUtils.checkAgentActive(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return walletService.getVouchers(token, serviceId);
    }

    @Override
    public List<ServiceOption> getProducts(KeycloakAuthenticationToken token, String serviceId) {
        agentManagementUtils.checkAgentActive(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return walletService.getProducts(token, serviceId);
    }

    @Override
    public List<ServiceOption> getSubscriptions(KeycloakAuthenticationToken token, String merchantCode, String serviceNumber, String serviceId) {
        agentManagementUtils.checkAgentActive(agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token)));
        return walletService.getSubscriptions(token, merchantCode, serviceNumber, serviceId);
    }

    @Override
    @Transactional
    public PayBillResponse payBill(KeycloakAuthenticationToken token, String uuid, PayBillRequest payBillRequest) {
        log.info("{} - Processing Request: {}", uuid, payBillRequest);
        LocalDateTime todayDateTime = LocalDateTime.now();
        String loggedInUserMobileNumber = AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token);
        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(loggedInUserMobileNumber);
        log.info("{} - agent found", uuid);
//        agentManagementUtils.checkAgentRightsForTransaction(agentDetails, AgentType.AGENT_BANKER);
        if (!agentDetails.getAgentPackage().getActive()) {
            throw new ObjectNotFoundException(ApiError.PACKAGE_NOT_ACTIVE);
        }
        TransferCommissionDto transferCommissionDto = agentManagementUtils.checkAgentLimitsForTransaction(uuid, "WALLET", payBillRequest.getType(),
                payBillRequest.getCurrencyName(), agentDetails, payBillRequest.getAmount(), payBillRequest.getFee(), todayDateTime);
        log.info("{} - Agent has Limits available to Process this Transaction", uuid);

        Transaction transaction = Transaction.builder().transactionKey(uuid).transactionDate(todayDateTime.toLocalDate())
                .transactionTime(todayDateTime.toLocalTime()).transactionType(payBillRequest.getType()).agentAuthorizationDetail(loggedInUserMobileNumber)
                .agentId(agentDetails.getAgentId()).amount(payBillRequest.getAmount()).currencyName(payBillRequest.getCurrencyName())
                .memberAgentCommission(transferCommissionDto.getAgentMemberCommission() != null ? transferCommissionDto.getAgentMemberCommission().toString() : "0")
                .agentCommission(transferCommissionDto.getAgentCommission() != null ? transferCommissionDto.getAgentCommission().toString() : "0")
                .superAgentCommission(transferCommissionDto.getAgentBankerCommission() != null ? transferCommissionDto.getAgentBankerCommission().toString() : "0")
                .memberWalletId(transferCommissionDto.getAgentMemberAccountNumber()).agentWalletId(transferCommissionDto.getAgentAccountNumber())
                .superAgentWalletId(transferCommissionDto.getAgentBankerAccountNumber())
                .commissionCredit(null).commissionCreditWallet(null).commissionDebit(null).commissionDebitWallet(null)
                .ledgerCredit(null).ledgerDebit(null).hostResponseCode(null).jsonResponse(null).transactionPerformedBy(agentDetails.getFullName()).build();
        log.info("{} - Transaction Log details are noted", uuid);

        log.info("{} - service call to bank start", uuid);
        PayBillResponse payBillResponse = walletService.payBill(token, uuid, payBillRequest);
        log.info("{} - service call to bank successful", uuid);
        transactionService.saveTransactionLog(transaction);
        log.info("{} - transaction log saved", uuid);
        return payBillResponse;
    }
}
