package com.digibank.agentmanagement.service;


import com.digibank.agentmanagement.deserializer.responses.ApiResponse;
import com.digibank.agentmanagement.deserializer.responses.FundTransferErrorResponse;
import com.digibank.agentmanagement.exception.ServiceException;
import com.digibank.agentmanagement.utils.AgentManagementConstants;
import com.digibank.agentmanagement.utils.AgentManagementPropertyHolder;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.accounttransfer.request.CashDepositRequest;
import com.digibank.agentmanagement.web.dto.accounttransfer.request.CashWithDrawlRequest;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.*;
import com.google.common.reflect.TypeToken;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class BankAdapterService {

    private final AgentManagementPropertyHolder agentManagementPropertyHolder;
    private final AgentManagementUtils agentManagementUtils;

    public HttpResponse<String> getAccountDetails(KeycloakAuthenticationToken token, String AccountNumber) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "account/{accountNumber}")
                        .routeParam("accountNumber", AccountNumber)
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        return response;
    }

    public HttpResponse<String> getCustomerDetails(KeycloakAuthenticationToken token, String customerId) {

        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "customer/{customer}")
                        .routeParam("customer", customerId)
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        return response;
    }

    public HttpResponse<String> getAccountList(KeycloakAuthenticationToken token, String customerId) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "customer/accounts/{customer}")
                        .routeParam("customer", customerId)
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        return response;
    }

    public CustomerDetailsResponse validateCustomer(KeycloakAuthenticationToken token, String customerId) {

        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "customer/{customer}")
                        .routeParam("customer", customerId)
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), CustomerDetailsResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public List<CustomerAccountsResponse> customerAccounts(KeycloakAuthenticationToken token, String customerId) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "customer/accounts/{customer}")
                        .routeParam("customer", customerId)
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<ArrayList<CustomerAccountsResponse>>(){}.getType());
//            AccountStatementResponse[] accountStatementResponse = agentManagementUtils.getGson().fromJson(response.getBody(), AccountStatementResponse[].class);
//            return Arrays.asList(accountStatementResponse);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

//    public HttpResponse<String> getAccountBalance(KeycloakAuthenticationToken token, String accountNumber) {
//        HttpResponse<String> response =
//                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "account/balance/{accountNumber}")
//                        .routeParam("accountNumber", accountNumber)
//                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
//                        .asString();
//        log.info("Response: {}", response.getBody());
//        return response;
//    }

    public AccountBalanceResponse getAccountBalance(KeycloakAuthenticationToken token, String accountNumber) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "account/balance/{accountNumber}")
                        .routeParam("accountNumber", accountNumber)
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), AccountBalanceResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

//    public HttpResponse<String> accountToAccountFundTransfer(KeycloakAuthenticationToken token, String sender, String receiver,
//                                                             String amountTransaction, String description, String mfaToken,
//                                                             String transactionType) {
//        log.info("{}", BankAdapterService.AccountToAccountTransfer.builder()
//                .sender(sender)
//                .receiver(receiver)
//                .amount(amountTransaction)
//                .description(description)
//                .mfaToken(mfaToken)
//                .type(transactionType)
//                .build().toString());
//
//        HttpResponse<String> response =
//                Unirest.post(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "txn")
//                        .header("Content-Type", "application/json")
//                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
//
//                        .body(BankAdapterService.AccountToAccountTransfer.builder()
//                                .sender(sender)
//                                .receiver(receiver)
//                                .amount(amountTransaction)
//                                .description(description)
//                                .mfaToken(mfaToken)
//                                .type("TRANSFER")
//                                .build()
//                        ).asString();
//
//        log.info("Response: {}", response.getBody());
//        return response;
//    }

    public CashDepositWithdrawResponse depositCash(String uuid, KeycloakAuthenticationToken token, CashDepositRequest cashDepositRequest) {
        log.info("{} - {}", uuid, cashDepositRequest);

        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "txn")
                        .header("Content-Type", "application/json")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .body(cashDepositRequest).asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), CashDepositWithdrawResponse.class);
        } else {
            FundTransferErrorResponse fundTransferErrorResponse = agentManagementUtils.getGson().fromJson(response.getBody(), FundTransferErrorResponse.class);
            ApiResponse apiResponse = ApiResponse.builder().errorCode(fundTransferErrorResponse.getCode()).title(fundTransferErrorResponse.getMessage()).detail(fundTransferErrorResponse.getCause()).build();
            throw new ServiceException(apiResponse);
        }
    }

    public CashDepositWithdrawResponse withdrawlCash(String uuid, KeycloakAuthenticationToken token, CashWithDrawlRequest cashWithDrawlRequest) {
        log.info("{} - {}", uuid, cashWithDrawlRequest);

        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "txn")
                        .header("Content-Type", "application/json")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .body(cashWithDrawlRequest).asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), CashDepositWithdrawResponse.class);
        } else {
            FundTransferErrorResponse fundTransferErrorResponse = agentManagementUtils.getGson().fromJson(response.getBody(), FundTransferErrorResponse.class);
            ApiResponse apiResponse = ApiResponse.builder().errorCode(fundTransferErrorResponse.getCode()).title(fundTransferErrorResponse.getMessage()).detail(fundTransferErrorResponse.getCause()).build();
            throw new ServiceException(apiResponse);
        }
    }

//    public HttpResponse<String> accountStatementRequest(KeycloakAuthenticationToken token, String fromDate, String toDate,
//                                                        String accountNumber, String pageNumber, String size) {
//        log.info("Request Params: {} {} {} {} {} {}", fromDate, toDate, accountNumber, pageNumber, size, ((KeycloakPrincipal) token.getPrincipal())
//                .getKeycloakSecurityContext()
//                .getTokenString());
//        HttpResponse<String> response =
//                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL()
//                                + "account/statement/{accountNumber}/{fromDate}/{toDate}/{page}/{size}")
//                        .routeParam("accountNumber", accountNumber)
//                        .routeParam("fromDate", fromDate)
//                        .routeParam("toDate", toDate)
//                        .routeParam("page", pageNumber)
//                        .routeParam("size", size)
//                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
//                        .asString();
//        log.info("Response: {}", response.getBody());
//        return response;
//    }

    public List<AccountStatementResponse> accountStatementRequest(KeycloakAuthenticationToken token, String accountNumber, String fromDate, String toDate,
                                                                  Integer pageNumber, Integer size) {
        log.info("Request Params: {} {} {} {} {} {}", fromDate, toDate, accountNumber, pageNumber, size, agentManagementUtils.getBearerTokenString(token));
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL()
                                + "account/statement/{accountNumber}/{fromDate}/{toDate}/{page}/{size}")
                        .routeParam("accountNumber", accountNumber)
                        .routeParam("fromDate", fromDate)
                        .routeParam("toDate", toDate)
                        .routeParam("page", pageNumber.toString())
                        .routeParam("size", size.toString())
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<ArrayList<AccountStatementResponse>>(){}.getType());
//            AccountStatementResponse[] accountStatementResponse = agentManagementUtils.getGson().fromJson(response.getBody(), AccountStatementResponse[].class);
//            return Arrays.asList(accountStatementResponse);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public CustomerAccountsResponse accountDetails(KeycloakAuthenticationToken token, String accountNumber) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "account/{accountNumber}")
                        .routeParam("accountNumber", accountNumber)
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<CustomerAccountsResponse>(){}.getType());
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder(toBuilder = true)
//    public static class AccountToAccountTransfer {
//        private String sender;
//        private String receiver;
//        private String amount;
//        private String description;
//        private String mfaToken;
//        private String type;
//    }
}
