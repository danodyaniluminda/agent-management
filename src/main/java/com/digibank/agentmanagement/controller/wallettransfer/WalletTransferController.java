package com.digibank.agentmanagement.controller.wallettransfer;

import com.digibank.agentmanagement.deserializer.responses.ExistingCustomerRegistration;
import com.digibank.agentmanagement.service.UserManagementAdapterService;
import com.digibank.agentmanagement.service.packagemanagement.WalletTransferService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.response.GenericTransactionRequest;
import com.digibank.agentmanagement.web.dto.usermanagement.response.WalletCustomerDetails;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.*;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/walletTransfers")
public class WalletTransferController {

    private final WalletTransferService walletTransferService;
    private final AgentManagementUtils agentManagementUtils;
    private final UserManagementAdapterService userManagementAdapterService;

    @Operation(description = "Wallet Account Opening")
    @PostMapping(value = "/openAccount", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExistingCustomerRegistration> openAccountOpening(KeycloakAuthenticationToken token, @Valid AccountOpenRequest accountOpenRequest) {
        log.info("Received request: {}", accountOpenRequest);
        return ResponseEntity.ok(walletTransferService.walletAccountOpeningRequest(token, accountOpenRequest));
    }

    @Operation(description = "Verify Pin")
    @PostMapping(value = "/verifyPin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyPin(@RequestBody @Valid VerifyPinRequest verifyPinRequest) {
        log.info("Received request: {}", verifyPinRequest);
        walletTransferService.verifyPin(verifyPinRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Resend Pin")
    @PostMapping(value = "/resendPin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resendPin(@RequestBody @Valid ResendPinRequest resendPinRequest) {
        log.info("Received request: {}", resendPinRequest);
        walletTransferService.resendPin(resendPinRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Set Password")
    @PostMapping(value = "/resetPassword", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        log.info("Received request: {}", resetPasswordRequest);
        walletTransferService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok().build();
    }


    @Operation(description = "Wallet History")
    @PostMapping(value = "/walletHistory")
    public ResponseEntity<?> walletHistory(KeycloakAuthenticationToken token, @RequestBody @Valid WalletHistoryRequest walletHistoryRequest) {
        log.info("Received request: {}", walletHistoryRequest);
        WalletHistoryPageResponse walletHistoryPageResponse = walletTransferService.walletHistory(token, walletHistoryRequest);
        return ResponseEntity.ok(walletHistoryPageResponse);
    }

    @Operation(description = "Wallet Account Balance")
    @PostMapping(value = "/walletBalance")
    public ResponseEntity<?> walletBalance(KeycloakAuthenticationToken token, @RequestBody @Valid WalletBalanceRequest walletBalanceRequest) {
        log.info("Received request: {}", walletBalanceRequest);
        List<CustomerBalanceResponse> customerBalanceResponses = walletTransferService.walletBalance(token, walletBalanceRequest);
        return ResponseEntity.ok(customerBalanceResponses);
    }

    @Operation(description = "Wallet To AFB")
    @PostMapping(value = "/WalletToAFB")
    public ResponseEntity<?> walletToAFB(@Valid GenericTransactionRequest transactionRequest) {
        log.info("Received request: {}", transactionRequest);
        walletTransferService.walletToAFB(transactionRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Wallet To Wallet")
    @PostMapping(value = "/walletToWallet")
    public ResponseEntity<?> walletToWallet(KeycloakAuthenticationToken token, @RequestBody @Valid WalletToWalletRequest walletToWalletRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, walletToWalletRequest);
        WalletToWalletResponse walletToWalletResponse = walletTransferService.walletToWallet(uuid, token, walletToWalletRequest);
        return ResponseEntity.ok(walletToWalletResponse);
    }

    @Operation(description = "Wallet Cash In")
    @PostMapping(value = "/walletCashIn")
    public ResponseEntity<?> walletCashIn(KeycloakAuthenticationToken token, @RequestBody @Valid WalletCashInRequest walletCashInRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, walletCashInRequest);
        WalletCashInResponse walletCashInResponse = walletTransferService.walletCashIn(uuid, token, walletCashInRequest);
        return ResponseEntity.ok(walletCashInResponse);
    }

    @Operation(description = "Wallet Cash Out")
    @PostMapping(value = "/walletCashOut")
    public ResponseEntity<?> walletCashOut(KeycloakAuthenticationToken token, @RequestBody @Valid WalletCashOutRequest walletCashOutRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, walletCashOutRequest);
        WalletCashOutResponse walletCashOutResponse = walletTransferService.walletCashOut(uuid, token, walletCashOutRequest);
        return ResponseEntity.ok(walletCashOutResponse);
    }

    @Operation(description = "Wallet History")
    @PostMapping(value = "/walletHistory2")
    public ResponseEntity<?> newWalletHistory2(KeycloakAuthenticationToken token, @RequestBody @Valid WalletHistoryRequest walletHistoryRequest) {
        log.info("Received request: {}", walletHistoryRequest);
        WalletHistoryPageResponse walletHistoryPageResponse = walletTransferService.walletHistory(token, walletHistoryRequest);
        return ResponseEntity.ok(walletHistoryPageResponse);
    }

    @Operation(description = "Wallet Account Balance")
    @GetMapping(value = "/walletBalance")
    public ResponseEntity<?> walletBalanceByLinkedAccountNumber(KeycloakAuthenticationToken token) {
        log.info("Received request: walletBalanceByLinkedAccountNumber");
        List<CustomerBalanceResponse> customerBalanceResponses = walletTransferService.walletBalanceByLinkedAccountNumber(token);
        return ResponseEntity.ok(customerBalanceResponses);
    }

    @Operation(description = "Wallet Account Balance")
    @GetMapping(value = "/walletUsername")
    public ResponseEntity<?> walletUsernameByPhoneNumber(KeycloakAuthenticationToken token) {
        log.info("Received request: walletUsernameByPhoneNumber");
        CustomerBalanceResponse customerBalanceResponse = walletTransferService.walletUsernameByPhoneNumber(token);
        return ResponseEntity.ok(customerBalanceResponse);
    }

    @Operation(description = "Wallet Customer Details")
    @GetMapping(value = "/walletCustomerDetails/{phoneNumber}")
    public ResponseEntity<?> walletCustomerDetails(KeycloakAuthenticationToken token, @PathVariable("phoneNumber") String phoneNumber) {
        log.info("Received request: walletCustomerDetails");
        WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token, phoneNumber);
        return ResponseEntity.ok(walletCustomerDetails);
    }

    @Operation(description = "Send Money To Non Digibank")
    @PostMapping(value = "/sendMoneyToNonSara")
    public ResponseEntity<?> sendMoneyToNonSara(KeycloakAuthenticationToken token, @RequestBody @Valid SendMoneyToNonWalletRequest sendMoneyToNonWalletRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, sendMoneyToNonWalletRequest);
        SendMoneyToNonWalletResponse sendMoneyToNonWalletResponse = walletTransferService.sendMoneyToNonWallet(uuid, token, sendMoneyToNonWalletRequest);
        return ResponseEntity.ok(sendMoneyToNonWalletResponse);
    }

    @Operation(description = "Send Money To Non Sara")
    @PostMapping(value = "/processPaymentDetails")
    public ResponseEntity<?> processPaymentDetails(KeycloakAuthenticationToken token, @RequestBody @Valid ProcessPaymentDetailsRequest processPaymentDetailsRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, processPaymentDetailsRequest);
        ProcessPaymentDetailsResponse processPaymentDetailsResponse = walletTransferService.processPaymentDetails(uuid, token, processPaymentDetailsRequest);
        return ResponseEntity.ok(processPaymentDetailsResponse);
    }

    @Operation(description = "Validate Customer")
    @PostMapping(value = "/validateCustomer")
    public ResponseEntity<?> validateCustomer(KeycloakAuthenticationToken token, @RequestBody @Valid ValidateCustomerRequest validateCustomerRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, validateCustomerRequest);
        WalletCustomerDetails walletCustomerDetails = walletTransferService.validateCustomer(uuid, token, validateCustomerRequest);
        return ResponseEntity.ok(walletCustomerDetails);
    }
}
