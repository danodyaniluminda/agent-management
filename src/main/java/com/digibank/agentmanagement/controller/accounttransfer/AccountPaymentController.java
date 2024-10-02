package com.digibank.agentmanagement.controller.accounttransfer;

import com.digibank.agentmanagement.service.accounttransfer.AccountPaymentService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.accounttransfer.request.AccountBillPayRequest;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.AccountBillPayResponse;
import com.digibank.agentmanagement.web.dto.common.Bill;
import com.digibank.agentmanagement.web.dto.common.PaymentMerchant;
import com.digibank.agentmanagement.web.dto.common.PaymentService;
import com.digibank.agentmanagement.web.dto.common.ServiceOption;
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
@RequestMapping("/api/accountPayments")
public class AccountPaymentController {

    private final AccountPaymentService accountPaymentService;
    private final AgentManagementUtils agentManagementUtils;

    @GetMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentService>> services(KeycloakAuthenticationToken token) {
        return ResponseEntity.ok(accountPaymentService.getServices(token));
    }

    @GetMapping(value = "/merchants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentMerchant>> merchants(KeycloakAuthenticationToken token) {
        return ResponseEntity.ok(accountPaymentService.getMerchants(token));
    }

    @GetMapping(value = "/bills", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Bill>> bill(KeycloakAuthenticationToken token, @RequestParam String merchantCode,
                                           @RequestParam String serviceNumber, @RequestParam String serviceId) {
        return ResponseEntity.ok(accountPaymentService.getBills(token, merchantCode, serviceNumber, serviceId));
    }

    @GetMapping(value = "/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServiceOption>> subscriptions(KeycloakAuthenticationToken token, @RequestParam String merchantCode,
                                                             @RequestParam String serviceNumber, @RequestParam String serviceId) {
        return ResponseEntity.ok(accountPaymentService.getSubscriptions(token, merchantCode, serviceNumber, serviceId));
    }

    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServiceOption>> products(KeycloakAuthenticationToken token, @RequestParam String serviceId) {
        return ResponseEntity.ok(accountPaymentService.getProducts(token, serviceId));
    }

    @GetMapping(value = "/vouchers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServiceOption>> vouchers(KeycloakAuthenticationToken token, @RequestParam String serviceId) {
        return ResponseEntity.ok(accountPaymentService.getVouchers(token, serviceId));
    }

    @PostMapping(value = "/payBill", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountBillPayResponse> payAccountBill(KeycloakAuthenticationToken token, @RequestBody @Valid AccountBillPayRequest accountBillPayRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, accountBillPayRequest);
        return ResponseEntity.ok(accountPaymentService.payBill(token, uuid, accountBillPayRequest));
    }
}
