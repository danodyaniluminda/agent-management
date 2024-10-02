package com.digibank.agentmanagement.controller.wallettransfer;

import com.digibank.agentmanagement.service.wallet.WalletPaymentService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.common.Bill;
import com.digibank.agentmanagement.web.dto.common.PaymentMerchant;
import com.digibank.agentmanagement.web.dto.common.PaymentService;
import com.digibank.agentmanagement.web.dto.common.ServiceOption;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.PayBillRequest;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.PayBillResponse;
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
@RequestMapping("/api/walletPayments")
public class WalletPaymentController {

    private final WalletPaymentService walletPaymentService;
    private final AgentManagementUtils agentManagementUtils;

    @GetMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentService>> services(KeycloakAuthenticationToken token) {
        return ResponseEntity.ok(walletPaymentService.getServices(token));
    }

    @GetMapping(value = "/merchants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentMerchant>> merchants(KeycloakAuthenticationToken token) {
        return ResponseEntity.ok(walletPaymentService.getMerchants(token));
    }

    @GetMapping(value = "/bills", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Bill>> bill(KeycloakAuthenticationToken token, @RequestParam String merchant,
                                           @RequestParam String serviceNumber, @RequestParam String serviceId) {
        return ResponseEntity.ok(walletPaymentService.getBills(token, merchant, serviceNumber, serviceId));
    }

    @GetMapping(value = "/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServiceOption>> subscriptions(KeycloakAuthenticationToken token, @RequestParam String merchantCode,
                                                             @RequestParam String serviceNumber, @RequestParam String serviceId) {
        return ResponseEntity.ok(walletPaymentService.getSubscriptions(token, merchantCode, serviceNumber, serviceId));
    }

    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServiceOption>> products(KeycloakAuthenticationToken token, @RequestParam String serviceId) {
        return ResponseEntity.ok(walletPaymentService.getProducts(token, serviceId));
    }

    @GetMapping(value = "/vouchers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ServiceOption>> vouchers(KeycloakAuthenticationToken token, @RequestParam String serviceId) {
        return ResponseEntity.ok(walletPaymentService.getVouchers(token, serviceId));
    }

    @PostMapping(value = "/payBill", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PayBillResponse> payBill(KeycloakAuthenticationToken token, @RequestBody @Valid PayBillRequest payBillRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, payBillRequest);
        return ResponseEntity.ok(walletPaymentService.payBill(token, uuid, payBillRequest));
    }
}
