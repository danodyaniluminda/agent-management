package com.digibank.agentmanagement.service;


import com.digibank.agentmanagement.deserializer.responses.*;
import com.digibank.agentmanagement.domain.RegistrationType;
import com.digibank.agentmanagement.exception.BadRequestException;
import com.digibank.agentmanagement.exception.ServiceException;
import com.digibank.agentmanagement.utils.AgentManagementConstants;
import com.digibank.agentmanagement.utils.AgentManagementPropertyHolder;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.common.Bill;
import com.digibank.agentmanagement.web.dto.common.PaymentMerchant;
import com.digibank.agentmanagement.web.dto.common.PaymentService;
import com.digibank.agentmanagement.web.dto.common.ServiceOption;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.*;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.*;
import com.google.common.reflect.TypeToken;
import kong.unirest.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Status;

import static com.digibank.agentmanagement.utils.ApiError.AGENT_ALREADY_EXISTS_WITH_THIS_PHONE_NUMBER;

@Service
@Slf4j
@AllArgsConstructor
public class WalletService {

    private final AgentManagementUtils agentManagementUtils;
    private final AgentManagementPropertyHolder agentManagementPropertyHolder;

    public HttpResponse<String> createAgentWallet(String walletUserId, String walletUserType, String countryCode, String currencyCode) {
        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api-internal/wallets")
                        .header("Content-Type", "application/json")
                        .body(WalletService.AgentWalletCreation.builder()
                                .walletUserId(walletUserId)
                                .walletUserType(walletUserType)
                                .countryCode(countryCode)
                                .currencyCode(currencyCode)
                                .build()
                        ).asString();

        log.info("Response: {}", response.getBody());
        return response;
    }

    public HttpResponse<String> walletToAFB(String accountNumber, String name, String amount, String reason, String mfaCode) {
        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api/transactions/walletToAFB")
                        //
                        .header("Content-Type", "application/json")
                        .body(WalletService.WalletToAFB.builder()
                                .beneficiaryAccountNumber(accountNumber)
                                .beneficiaryName(name)
                                .amount(amount)
                                .reason(reason)
                                .mfaToken(mfaCode)
                                .build()
                        ).asString();

        log.info("Response: {}", response.getBody());
        return response;
    }

//    public HttpResponse<String> walletToWallet(String beneficiaryWalletId, String amount, String currencyCode, String reason, String mfaCode) {
//        HttpResponse<String> response =
//                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api/transactions/walletToWallet")
//                        //
//                        .header("Content-Type", "application/json")
//                        .body(WalletService.WalletToWallet.builder()
//                                .beneficiaryWalletId(beneficiaryWalletId)
//                                .amount(amount)
//                                .currencyCode(currencyCode)
//                                .reason(reason)
//                                .mfaToken(mfaCode)
//                                .build()
//                        ).asString();
//
//        log.info("Response: {}", response.getBody());
//        return response;
//    }

//    public HttpResponse<String> walletCashIn(String fromAccountNumber, String amount, String reason, String mfaCode) {
//        HttpResponse<String> response =
//                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api/transactions/cashIn")
//                        //
//                        .header("Content-Type", "application/json")
//                        .body(WalletService.WalletCashIn.builder()
//                                .fromAccountNumber(fromAccountNumber)
//                                .amount(amount)
//                                .reason(reason)
//                                .mfaToken(mfaCode)
//                                .build()
//                        ).asString();
//
//        log.info("Response: {}", response.getBody());
//        return response;
//    }

    public WalletCashInResponse walletCashIn(String uuid, KeycloakAuthenticationToken token, WalletCashInRequest walletCashInRequest) {
        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api-internal/transactions/cash-in")
                        //.header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .header("Content-Type", "application/json")
                        .body(WalletCashIn.builder().reason(walletCashInRequest.getReason())
                                .userType(walletCashInRequest.getUserType())
                                .userId(walletCashInRequest.getUserId())
                                .amount(walletCashInRequest.getAmount())
                                .debtorBankAccountNumber(walletCashInRequest.getDebtorBankAccountNumber()).build())
                        .asString();

        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), WalletCashInResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public WalletCashOutResponse walletCashOut(String uuid, KeycloakAuthenticationToken token, WalletCashOutRequest walletCashOutRequest) {
        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api-internal/transactions/cash-out")
                        .header("Content-Type", "application/json")
                        //.header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .body(WalletCashOut.builder().amount(walletCashOutRequest.getAmount())
                                .reason(walletCashOutRequest.getReason())
                                .userType(walletCashOutRequest.getUserType())
                                .userId(walletCashOutRequest.getUserId())
                                .toAccountNumber(walletCashOutRequest.getToAccountNumber()).build()
                        ).asString();

        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), WalletCashOutResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public WalletToWalletResponse walletToWallet(String uuid, KeycloakAuthenticationToken token, WalletToWalletRequest walletToWalletRequest) {
        log.info("Response: {}", walletToWalletRequest);
        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api-internal/transactions/wallet-to-wallet")
                        //.header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .header("Content-Type", "application/json")
                        .body(WalletToWallet.builder().amount(walletToWalletRequest.getAmount())
                                .creditorUserId(walletToWalletRequest.getCreditorUserId())
                                .creditorUserType(walletToWalletRequest.getCreditorUserType())
                                .debtorUserId(walletToWalletRequest.getDebtorUserId())
                                .debtorUserType(walletToWalletRequest.getDebtorUserType())
                                .reason(walletToWalletRequest.getReason())
                                .currencyCode(walletToWalletRequest.getCurrencyCode()).build()
                        )
                        .asString();

        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), WalletToWalletResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

//    public HttpResponse<String> walletCashOut(String toAccountNumber, String amount, String reason, String mfaCode) {
//        HttpResponse<String> response =
//                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api/transactions/cashOut")
//                        //
//                        .header("Content-Type", "application/json")
//                        .body(WalletService.WalletCashOut.builder()
//                                .toAccountNumber(toAccountNumber)
//                                .amount(amount)
//                                .reason(reason)
//                                .mfaToken(mfaCode)
//                                .build()
//                        ).asString();
//
//        log.info("Response: {}", response.getBody());
//        return response;
//    }

    public ExistingCustomerRegistration customerRegistration(AccountOpenRequest accountOpenRequest) {



        MultipartBody multipartBodyRequest = Unirest.post(agentManagementPropertyHolder.getUserManagementURL() + "api-public/registration/customer")
                .field(AgentManagementConstants.REGISTRATION_TYPE, accountOpenRequest.getRegistrationType())
                .field(AgentManagementConstants.PHONE_NUMBER_COUNTRY_CODE, accountOpenRequest.getPhoneNumberCountryCode())
                .field(AgentManagementConstants.COUNTRY_CODE, accountOpenRequest.getCurrencyCode())
                .field(AgentManagementConstants.PHONE_NUMBER, accountOpenRequest.getPhoneNumber())
                .field(AgentManagementConstants.ID_DOCUMENT_TYPE, accountOpenRequest.getIdDocumentType().toString())
                .field(AgentManagementConstants.ID_DOCUMENT_NUMBER, accountOpenRequest.getIdDocumentNumber())
                .field(AgentManagementConstants.ID_DOCUMENT_EXPIRY_DATE, accountOpenRequest.getIdDocumentExpiryDate().toString())
                .field(AgentManagementConstants.EMAIL_ADDRESS, accountOpenRequest.getEmailAddress())
                .field(AgentManagementConstants.UIN, accountOpenRequest.getUin())
                .field(AgentManagementConstants.TC_ACCEPTED, String.valueOf(accountOpenRequest.isTcAccepted()))
                .field(AgentManagementConstants.LOCALE, accountOpenRequest.getLocale().toString())
                .field(AgentManagementConstants.CURRENCY_CODE, accountOpenRequest.getCurrencyCode())
                .field(AgentManagementConstants.AGENT_BANKER_PHONE_NUMBER, accountOpenRequest.getAgentBankerPhoneNumber())
                .field(AgentManagementConstants.DATE_OF_BIRTH, accountOpenRequest.getDateOfBirth().toString())
                .field(AgentManagementConstants.ADDRESS, accountOpenRequest.getAddress())
                .field(AgentManagementConstants.CITY_OF_RESIDENCE, accountOpenRequest.getCityOfResidence());
        if (accountOpenRequest.getRegistrationType() == RegistrationType.NON_EXISTING_BANK_CUSTOMER) {
            multipartBodyRequest.field(AgentManagementConstants.FIRST_NAME, accountOpenRequest.getFirstName());
            multipartBodyRequest.field(AgentManagementConstants.LAST_NAME, accountOpenRequest.getLastName());
        }
        else
        {
            multipartBodyRequest.field(AgentManagementConstants.BANK_CUSTOMER_ID, accountOpenRequest.getBankCustomerId());
        }
        List<File> filesToBeRemoved = new ArrayList<>();
        try {
            if (accountOpenRequest.getSelfieDocumentFile() == null && CollectionUtils.isEmpty(accountOpenRequest.getIdDocumentFile())) {
                multipartBodyRequest.field("tmpFile", File.createTempFile(AgentManagementConstants.JAVA_TMP_PATH, "tmpFile.txt"));
            } else {
                if (accountOpenRequest.getSelfieDocumentFile() != null) {
                    File convertedFile = new File(AgentManagementConstants.JAVA_TMP_PATH + accountOpenRequest.getSelfieDocumentFile().getOriginalFilename());
                    accountOpenRequest.getSelfieDocumentFile().transferTo(convertedFile);
                    multipartBodyRequest.field(AgentManagementConstants.SELFIE_DOCUMENT_FILE, convertedFile);
                    filesToBeRemoved.add(convertedFile);
                }
                if (accountOpenRequest.getIdDocumentFile() != null && accountOpenRequest.getIdDocumentFile().size() > 0) {
                    for (MultipartFile multipartFile : accountOpenRequest.getIdDocumentFile()) {
                        File convertedFile = new File(AgentManagementConstants.JAVA_TMP_PATH + multipartFile.getOriginalFilename());
                        multipartFile.transferTo(convertedFile);
                        multipartBodyRequest.field(AgentManagementConstants.ID_DOCUMENT_FILE, convertedFile);
                        filesToBeRemoved.add(convertedFile);
                    }
                }
            }
            HttpResponse<String> response = multipartBodyRequest.asString();
            log.info("Response: {}", response.getBody());
            if (response.isSuccess()) {
                return agentManagementUtils.getGson().fromJson(response.getBody(), ExistingCustomerRegistration.class);
            } else {
                ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
                //throw new BadRequestException(apiResponse.getDetail(), Status.valueOf(apiResponse.getStatus())  , apiResponse.getErrorCode());
                throw new ServiceException(apiResponse);

            }
        } catch (IOException ioException) {
            String error = "Error creating tmpFile at Path: " + AgentManagementConstants.JAVA_TMP_PATH;
            log.error(error, ioException);
            throw new ServiceException(ApiError.SERVICE_ERROR);
        } finally {
            filesToBeRemoved.forEach(File::delete);
        }
    }

    public void verifyPin(VerifyPinRequest verifyPinRequest) {
        HttpResponse<String> response = Unirest.post(agentManagementPropertyHolder.getUserManagementURL() + "api-public/registration/customer/verifyPIN")
                .header("Content-Type", "application/json")
                .body(verifyPinRequest).asString();
        log.info("Response: {}", response.getBody());
        if (!response.isSuccess()) {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public void resendPin(ResendPinRequest resendPinRequest) {
        HttpResponse<String> response = Unirest.post(agentManagementPropertyHolder.getUserManagementURL() + "api-public/registration/customer/resendPIN")
                .header("Content-Type", "application/json")
                .body(resendPinRequest).asString();
        log.info("Response: {}", response.getBody());
        if (!response.isSuccess()) {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public void setPassword(ResetPasswordRequest resetPasswordRequest) {
        HttpResponse<String> response = Unirest.post(agentManagementPropertyHolder.getUserManagementURL() + "api-public/registration/customer/setPassword")
                .header("Content-Type", "application/json")
                .body(resetPasswordRequest).asString();
        log.info("Response: {}", response.getBody());
        if (!response.isSuccess()) {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public List<CustomerBalanceResponse> walletBalance(KeycloakAuthenticationToken token, String userType, String userId) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "/api-internal/wallets/balance")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .queryString("userType", userType).queryString("userId", userId).asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<ArrayList<CustomerBalanceResponse>>() {}.getType());
//            AccountStatementResponse[] accountStatementResponse = agentManagementUtils.getGson().fromJson(response.getBody(), AccountStatementResponse[].class);
//            return Arrays.asList(accountStatementResponse);
        } else {
            ApiResponse2 apiResponse2 = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse2.class);
            ApiResponse apiResponse = ApiResponse.builder().status(apiResponse2.getStatus()).title(apiResponse2.getError()).detail(apiResponse2.getMessage()).build();
            throw new ServiceException(apiResponse);
        }
    }

    public WalletHistoryPageResponse walletHistory(KeycloakAuthenticationToken token, WalletHistoryRequest walletHistoryRequest) {

        log.info("Request: {}", walletHistoryRequest);
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "/api-internal/wallets/history")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .queryString("currencyCodes", walletHistoryRequest.getCurrencyCodes())
                        .queryString("fromDate", walletHistoryRequest.getFromDate())
                        .queryString("toDate", walletHistoryRequest.getToDate())
                        .queryString("page", walletHistoryRequest.getPage())
                        .queryString("size", walletHistoryRequest.getSize())
                        .queryString("userType", walletHistoryRequest.getUserType())
                        .queryString("userId", walletHistoryRequest.getUserId())
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), WalletHistoryPageResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public List<CustomerBalanceResponse> getAccountBalanceByLinkedAccountNumber(KeycloakAuthenticationToken token, String accountNumber) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "account/balance/{accountNumber}")
                        .routeParam("accountNumber", accountNumber)
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<ArrayList<CustomerBalanceResponse>>() {}.getType());
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public CustomerBalanceResponse getWalletUsernameByPhoneNumber(KeycloakAuthenticationToken token, String phoneNumber) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "account/balance/{accountNumber}")
                        .routeParam("phoneNumber", phoneNumber)
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), CustomerBalanceResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public SendMoneyToNonWalletResponse sendMoneyToNonWallet(KeycloakAuthenticationToken token, SendMoneyToNonWalletRequest sendMoneyToNonWalletRequest) {
        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api-internal/transactions/sendMoneyToNonWallet")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .body(sendMoneyToNonWalletRequest).asString();

        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), SendMoneyToNonWalletResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public PaymentDetailsResponse paymentDetails(KeycloakAuthenticationToken token, ProcessPaymentDetailsRequest processPaymentDetailsRequest) {
        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api-internal/transactions/getPaymentDetails")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .body(processPaymentDetailsRequest).asString();

        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), PaymentDetailsResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public ProcessPaymentDetailsResponse processPaymentDetails(KeycloakAuthenticationToken token, ProcessPaymentDetailsRequest processPaymentDetailsRequest) {
        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api-internal/transactions/processPaymentDetails")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .body(processPaymentDetailsRequest).asString();

        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), ProcessPaymentDetailsResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public List<PaymentService> getServices(KeycloakAuthenticationToken token) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api/payment/services")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<ArrayList<PaymentService>>() {}.getType());
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public List<PaymentMerchant> getMerchants(KeycloakAuthenticationToken token) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api/payment/merchants")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<ArrayList<PaymentMerchant>>() {}.getType());
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public List<Bill> getBills(KeycloakAuthenticationToken token, String merchant, String serviceNumber, String serviceId) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "/api/payment/bill")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .queryString("merchant", merchant)
                        .queryString("serviceNumber", serviceNumber)
                        .queryString("serviceId", serviceId)
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<ArrayList<Bill>>() {
            }.getType());
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public List<ServiceOption> getVouchers(KeycloakAuthenticationToken token, String serviceId) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "/api/payment/voucher")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .queryString("serviceId", serviceId)
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<ArrayList<ServiceOption>>() {
            }.getType());
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public List<ServiceOption> getProducts(KeycloakAuthenticationToken token, String serviceId) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "/api/payment/product")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .queryString("serviceId", serviceId)
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<ArrayList<ServiceOption>>() {
            }.getType());
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public List<ServiceOption> getSubscriptions(KeycloakAuthenticationToken token, String merchantCode, String serviceNumber, String serviceId) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "/api/payment/subscription")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .queryString("merchantCode", merchantCode)
                        .queryString("serviceNumber", serviceNumber)
                        .queryString("serviceId", serviceId)
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<ArrayList<ServiceOption>>() {
            }.getType());
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public PayBillResponse payBill(KeycloakAuthenticationToken token, String uuid, PayBillRequest payBillRequest) {
        log.info("{} - Calling Backend: {}", uuid, payBillRequest);
        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getWalletAdaptorServiceURL() + "api/payment/pay")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .header("Content-Type", "application/json")
                        .body(payBillRequest).asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), PayBillResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class AgentWalletCreation {
        private String walletUserId;
        private String walletUserType;
        private String countryCode;
        private String currencyCode;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class WalletToAFB {
        private String beneficiaryAccountNumber;
        private String beneficiaryName;
        private String amount;
        private String reason;
        private String mfaToken;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class WalletToWallet {
        private String debtorUserType;
        private String debtorUserId;
        private String currencyCode;
        private BigDecimal amount;
        private String reason;
        private String creditorUserType;
        private String creditorUserId;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class WalletCashIn {
        private String debtorBankAccountNumber;
        private BigDecimal amount;
        private String reason;
        private String userType;
        private String userId;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class WalletCashOut {
        private String toAccountNumber;
        private BigDecimal amount;
        private String reason;
        private String userType;
        private String userId;

    }
}
