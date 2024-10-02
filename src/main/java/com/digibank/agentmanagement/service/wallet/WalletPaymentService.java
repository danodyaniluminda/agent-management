package com.digibank.agentmanagement.service.wallet;

import com.digibank.agentmanagement.web.dto.common.Bill;
import com.digibank.agentmanagement.web.dto.common.PaymentMerchant;
import com.digibank.agentmanagement.web.dto.common.PaymentService;
import com.digibank.agentmanagement.web.dto.common.ServiceOption;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.PayBillRequest;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.PayBillResponse;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

import java.util.List;

public interface WalletPaymentService {

    List<PaymentService> getServices(KeycloakAuthenticationToken token);

    List<PaymentMerchant> getMerchants(KeycloakAuthenticationToken token);

    List<Bill> getBills(KeycloakAuthenticationToken token, String merchant, String serviceNumber, String serviceId);

    List<ServiceOption> getVouchers(KeycloakAuthenticationToken token, String serviceId);

    List<ServiceOption> getProducts(KeycloakAuthenticationToken token, String serviceId);

    List<ServiceOption> getSubscriptions(KeycloakAuthenticationToken token, String merchantCode, String serviceNumber, String serviceId);

    PayBillResponse payBill(KeycloakAuthenticationToken token, String uuid, PayBillRequest payBillRequest);
}
