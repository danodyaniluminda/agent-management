package com.digibank.agentmanagement.controller.accounttransfer;

import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.service.packagemanagement.AccountTransactionService;
import com.digibank.agentmanagement.service.packagemanagement.AgentLimitProfileService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.dto.accounttransfer.request.*;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class AccountTransactionController {

    private final AccountTransactionService accountTransactionService;
    private final AgentManagementUtils agentManagementUtils;

    @Autowired
    private AgentLimitProfileService agentLimitProfileService;

    @PostMapping(value = "/agencyBanking/accountBalance")
    public ResponseEntity<?> accountBalance(KeycloakAuthenticationToken token, @RequestBody @Valid BalanceInquiryRequest balanceInquiryRequest) {
        log.info("accountBalance request: {}", balanceInquiryRequest);
        if (StringUtils.isEmpty(balanceInquiryRequest.getAccountNumber())) {
            throw new InvalidInputException(ApiError.INVALID_INPUT_PARAMETER);
        }

        AccountBalanceResponse accountBalanceResponse = accountTransactionService.getAccountBalance(token, balanceInquiryRequest);
        return ResponseEntity.ok(accountBalanceResponse);
    }

    @PostMapping(value = "/agencyBanking/accountStatement")
    public ResponseEntity<?> accountStatement(KeycloakAuthenticationToken token, @RequestBody @Valid AccountStatementRequest accountStatementRequest) {
        log.debug("accountStatement request: {}", accountStatementRequest);
        if (StringUtils.isEmpty(accountStatementRequest.getAccountNumber())) {
            throw new InvalidInputException(ApiError.INVALID_INPUT_PARAMETER);
        }
        List<AccountStatementResponse> accountStatementResponse = accountTransactionService.getAccountStatement(
                token, accountStatementRequest);
        return ResponseEntity.ok(accountStatementResponse);
    }

    @GetMapping(value = "/agencyBanking/accountDetails/{accountNumber}")
    public ResponseEntity<?> accountDetails(KeycloakAuthenticationToken token, @PathVariable String accountNumber
    ) {
        log.debug("accountStatement request: {}", accountNumber);
        if (StringUtils.isEmpty(accountNumber)) {
            throw new InvalidInputException(ApiError.INVALID_INPUT_PARAMETER);
        }
        CustomerAccountsResponse accountDetails = accountTransactionService.getAccountDetails(
                token, accountNumber);
        return ResponseEntity.ok(accountDetails);
    }

    @GetMapping(value = "/agencyBanking/agentAccountBalance/{accountNumber}")
    public ResponseEntity<?> accountBalanceAgent(KeycloakAuthenticationToken token, @PathVariable String accountNumber) {
        log.debug("Agent accountBalance request");
        AccountBalanceResponse accountBalanceResponse = accountTransactionService.getAgentAccountBalance(token, accountNumber);
        return ResponseEntity.ok(accountBalanceResponse);
    }

    @GetMapping(value = "/agencyBanking/agentAccountStatement")
    public ResponseEntity<?> accountStatementAgent(KeycloakAuthenticationToken token,
                                                   @RequestParam @NotBlank String fromDate, @RequestParam @NotBlank String toDate,
                                                   @RequestParam @NotNull Integer pageNumber, @RequestParam @NotNull Integer size, @RequestParam @NotNull String accountNumber) {
        log.debug("Agent accountStatement request");
        List<AccountStatementResponse> accountStatementResponse = accountTransactionService.getAgentAccountStatement(
                token, fromDate, toDate, pageNumber, size, accountNumber);
        return ResponseEntity.ok(accountStatementResponse);
    }

    @PostMapping(value = "/agencyBanking/cashDeposit")
    public ResponseEntity<?> cashDeposit(KeycloakAuthenticationToken token, @RequestBody @Valid CashDepositRequest cashDepositRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, cashDepositRequest);
        if (cashDepositRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT_OBJECT);
        }
        CashDepositWithdrawResponse cashDepositWithdrawResponse = accountTransactionService.cashDepositToAccount(uuid, token, cashDepositRequest);
        return ResponseEntity.ok(cashDepositWithdrawResponse);
    }

    @PostMapping(value = "/agencyBanking/cashWithdrawal")
    public ResponseEntity<?> cashWithdrawal(KeycloakAuthenticationToken token, @RequestBody @Valid CashWithDrawlRequest cashWithDrawlRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, cashWithDrawlRequest);
        if (cashWithDrawlRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT_OBJECT);
        }
        CashDepositWithdrawResponse cashDepositWithdrawResponse = accountTransactionService.cashWithdrawalFromAccount(uuid, token, cashWithDrawlRequest);
        return ResponseEntity.ok(cashDepositWithdrawResponse);
    }

    @GetMapping(value = "/agencyBanking/validateCustomer/{customerId}")
    public ResponseEntity<?> validateCustomer(KeycloakAuthenticationToken token, @PathVariable String customerId) {
        log.info("Received request: {}", customerId);
        if (customerId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT_OBJECT);
        }
        CustomerDetailsResponse customerDetailsResponse = accountTransactionService.validateCustomer(token, customerId);
        return ResponseEntity.ok(customerDetailsResponse);
    }

    @GetMapping(value = "/agencyBanking/validateAgentLimit")
    public ResponseEntity<?> validateAgentLimit(KeycloakAuthenticationToken token, @RequestBody @Valid AgentLimitValidationRequest limitValidationRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, limitValidationRequest);
        if (limitValidationRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT_OBJECT);
        }
        AgentLimitValidationResponse agentLimitValidationResponse = agentLimitProfileService.validateAgentLimit(uuid, token, limitValidationRequest);
        return ResponseEntity.ok(agentLimitValidationResponse);
    }

    @GetMapping(value = "/agencyBanking/customerAccounts/{customerId}")
    public ResponseEntity<?> customerAccounts(KeycloakAuthenticationToken token, @PathVariable String customerId) {
        log.info("Received request: {}", customerId);
        if (customerId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT_OBJECT);
        }
        List<CustomerAccountsResponse> customerAccountsResponses = accountTransactionService.customerAccounts(token, customerId);
        return ResponseEntity.ok(customerAccountsResponses);
    }

    @PostMapping(value = "/agencyBanking/accountOpen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> accountOpen(KeycloakAuthenticationToken token, @Valid AccountOpenRequest accountOpenRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, accountOpenRequest);
        if (accountOpenRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT_OBJECT);
        }
        AccountOpenResponse accountOpenResponse = accountTransactionService.openAccount(uuid, token, accountOpenRequest);
        return ResponseEntity.ok(accountOpenResponse);
    }
}
