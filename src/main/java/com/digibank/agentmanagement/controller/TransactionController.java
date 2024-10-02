package com.digibank.agentmanagement.controller;


import com.digibank.agentmanagement.service.TransactionService;
import com.digibank.agentmanagement.web.dto.response.AccountOpeningReq;
import com.digibank.agentmanagement.web.dto.response.GenericTransactionRequest;
import com.digibank.agentmanagement.web.dto.response.TransactionRes;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
public class TransactionController {


    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @Operation(description = "Core Bank Account Opening")
    @PostMapping(
            value = "/api-public/AgencyBanking/coreAccountOpening")
    public ResponseEntity<TransactionRes> coreAccountOpening(KeycloakAuthenticationToken token, @Valid AccountOpeningReq accountOpeningReq) {
        log.info("Received request: {}", accountOpeningReq);
        TransactionRes transactionRes = transactionService.coreAccountOpeningRequest(token, accountOpeningReq);
        return ResponseEntity.ok(transactionRes);
    }


//    @Operation(description = "Get Account Balance")
//    @PostMapping(
//            value = "/api-public/AgencyBanking/AccountBalance")
//    public ResponseEntity<TransactionRes> getAccountBalance(KeycloakAuthenticationToken token, @Valid GenericTransactionRequest transactionRequest)
//    {
//        log.info("Received request: {}", transactionRequest);
//        TransactionRes transactionRes = transactionService.accountBalanceRequest(token  , transactionRequest);
//        return ResponseEntity.ok(transactionRes);
//    }

//    @Operation(description = "Account To Account Fund transfer")
//    @PostMapping(
//            value = "/api-public/AgencyBanking/AccountToAccountTransfer")
//    public ResponseEntity<TransactionRes> accountToAccountFT(KeycloakAuthenticationToken token,
//                                                             @RequestBody @Valid GenericTransactionRequest transactionRequest)
//    {
//        log.info("Received request: {}", transactionRequest);
//        TransactionRes transactionRes = transactionService.accountToAccountTransfer(token , transactionRequest);
//        return ResponseEntity.ok(transactionRes);
//
//    }

//    @Operation(description = "Cash Deposit From Account")
//    @PostMapping(
//            value = "/api-public/AgencyBanking/CashDeposit")
//    public ResponseEntity<TransactionRes> accountCashDeposit(KeycloakAuthenticationToken token,
//                                                             @RequestBody @Valid GenericTransactionRequest transactionRequest)
//    {
//        log.info("Received request: {}", transactionRequest);
//        TransactionRes transactionRes = transactionService.accountCashDeposit(token , transactionRequest);
//        return ResponseEntity.ok(transactionRes);
//
//    }
//
//    @Operation(description = "Cash Withdrawal From Account")
//    @PostMapping(
//            value = "/api-public/AgencyBanking/CashWithdrawal")
//    public ResponseEntity<TransactionRes> accountCashWithdrawal(KeycloakAuthenticationToken token,
//                                                             @RequestBody @Valid GenericTransactionRequest transactionRequest)
//    {
//        log.info("Received request: {}", transactionRequest);
//        TransactionRes transactionRes = transactionService.accountCashWithdrawal(token , transactionRequest);
//        return ResponseEntity.ok(transactionRes);
//
//    }

//    @Operation(description = "Account Statement")
//    @PostMapping(
//            value = "/api-public/AgencyBanking/AccountStatement")
//    public ResponseEntity<TransactionRes> accountStatement(KeycloakAuthenticationToken token,
//                                                                @Valid GenericTransactionRequest transactionRequest)
//    {
//        log.info("Received request: {}", transactionRequest);
//        TransactionRes transactionRes = transactionService.accountStatement(token , transactionRequest);
//        return ResponseEntity.ok(transactionRes);
//    }

    @Operation(description = "MFA Token Generation For Financial Transaction")
    @PostMapping(
            value = "/api-public/AgencyBanking/MFAGenerationForTransaction")
    public ResponseEntity MFAGenerationForTransaction(KeycloakAuthenticationToken token,
                                                      @RequestBody @Valid GenericTransactionRequest transactionRequest) {
        log.info("Received request: {}", transactionRequest);
        //CustomerDetailsResponse customerDetailsResponse = transactionService.generateMFAForTransaction(token , transactionRequest);
        transactionService.generateMFAForTransaction(token, transactionRequest);
        return ResponseEntity.ok().build();
    }


}
