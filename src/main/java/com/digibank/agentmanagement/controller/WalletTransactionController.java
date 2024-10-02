/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digibank.agentmanagement.controller;

import com.digibank.agentmanagement.deserializer.responses.ExistingCustomerRegistration;
import com.digibank.agentmanagement.service.TransactionService;
import com.digibank.agentmanagement.web.dto.response.GenericTransactionRequest;
import com.digibank.agentmanagement.web.dto.response.TransactionRes;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.AccountOpenRequest;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.ResendPinRequest;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.ResetPasswordRequest;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.VerifyPinRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author ali-r
 */
@RestController
@Slf4j
public class WalletTransactionController {

    private final TransactionService transactionService;

    public WalletTransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(description = "Wallet Account Opening")
    @PostMapping(
            value = "/api-public/WalletTransactions/walletAccountOpening",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExistingCustomerRegistration> walletAccountOpening(KeycloakAuthenticationToken token, @Valid AccountOpenRequest accountOpeningReq) {
        log.info("Received request: {}", accountOpeningReq);
        return ResponseEntity.ok(transactionService.walletAccountOpeningRequest(token, accountOpeningReq));
    }

    @Operation(description = "Verify Pin")
    @PostMapping(
            value = "/api-public/WalletTransactions/verifyPin",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyPin(KeycloakAuthenticationToken token,
                                       @RequestBody @Valid VerifyPinRequest verifyPinRequest) {
        log.info("Received request: {}", verifyPinRequest);
        transactionService.verifyPin(token, verifyPinRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Resend Pin")
    @PostMapping(
            value = "/api-public/WalletTransactions/resendPin",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resendPin(KeycloakAuthenticationToken token,
                                       @RequestBody @Valid ResendPinRequest resendPinRequest) {
        log.info("Received request: {}", resendPinRequest);
        transactionService.resendPin(token, resendPinRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Set Password")
    @PostMapping(
            value = "/api-public/WalletTransactions/setPassword",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setPassword(KeycloakAuthenticationToken token,
                                         @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        log.info("Received request: {}", resetPasswordRequest);
        transactionService.setPassword(token, resetPasswordRequest);
        return ResponseEntity.ok().build();
    }


    @Operation(description = "Wallet Account Statement")
    @PostMapping(
            value = "/api-public/WalletTransactions/WalletAccountStatement")
    public ResponseEntity<TransactionRes> walletAccountStatement(KeycloakAuthenticationToken token,
                                                                 @Valid GenericTransactionRequest transactionRequest) {
        log.info("Received request: {}", transactionRequest);
        TransactionRes transactionRes = transactionService.walletAccountStatement(token, transactionRequest);
        return ResponseEntity.ok(transactionRes);

    }


    @Operation(description = "Wallet Account Balance")
    @PostMapping(
            value = "/api-public/WalletTransactions/WalletAccountBalance")
    public ResponseEntity<TransactionRes> walletAccountBalance(KeycloakAuthenticationToken token,
                                                               @Valid GenericTransactionRequest transactionRequest) {
        log.info("Received request: {}", transactionRequest);
        TransactionRes transactionRes = transactionService.walletAccountBalance(token, transactionRequest);
        return ResponseEntity.ok(transactionRes);

    }

    @Operation(description = "Wallet To AFB")
    @PostMapping(
            value = "/api-public/WalletTransactions/WalletToAFB")
    public ResponseEntity<TransactionRes> walletToAFB(KeycloakAuthenticationToken token,
                                                      @Valid GenericTransactionRequest transactionRequest) {
        log.info("Received request: {}", transactionRequest);
        TransactionRes transactionRes = transactionService.walletToAFB(token, transactionRequest);
        return ResponseEntity.ok(transactionRes);

    }

    @Operation(description = "Wallet To Wallet")
    @PostMapping(
            value = "/api-public/WalletTransactions/WalletToWallet")
    public ResponseEntity<TransactionRes> walletToWallet(KeycloakAuthenticationToken token,
                                                         @Valid GenericTransactionRequest transactionRequest) {
        log.info("Received request: {}", transactionRequest);
        TransactionRes transactionRes = transactionService.walletToWallet(token, transactionRequest);
        return ResponseEntity.ok(transactionRes);

    }


    @Operation(description = "Wallet Cash In")
    @PostMapping(
            value = "/api-public/WalletTransactions/WalletCashIn")
    public ResponseEntity<TransactionRes> walletCashIn(KeycloakAuthenticationToken token,
                                                       @Valid GenericTransactionRequest transactionRequest) {
        log.info("Received request: {}", transactionRequest);
        TransactionRes transactionRes = transactionService.walletCashIn(token, transactionRequest);
        return ResponseEntity.ok(transactionRes);

    }

    @Operation(description = "Wallet Cash Out")
    @PostMapping(
            value = "/api-public/WalletTransactions/WalletCashOut")
    public ResponseEntity<TransactionRes> walletCashOut(KeycloakAuthenticationToken token,
                                                        @Valid GenericTransactionRequest transactionRequest) {
        log.info("Received request: {}", transactionRequest);
        TransactionRes transactionRes = transactionService.walletCashOut(token, transactionRequest);
        return ResponseEntity.ok(transactionRes);

    }


}
