package com.digibank.agentmanagement.service.packagemanagement;

import com.digibank.agentmanagement.deserializer.responses.ExistingCustomerRegistration;
import com.digibank.agentmanagement.web.dto.usermanagement.response.WalletCustomerDetails;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.*;
import com.digibank.agentmanagement.web.dto.response.GenericTransactionRequest;
import com.digibank.agentmanagement.web.dto.response.TransactionRes;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.*;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

import java.util.List;

public interface WalletTransferService {

    ExistingCustomerRegistration walletAccountOpeningRequest(KeycloakAuthenticationToken token, AccountOpenRequest accountOpeningReq);

    void verifyPin(VerifyPinRequest verifyPinRequest);

    void resendPin(ResendPinRequest resendPinRequest);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);

    WalletHistoryPageResponse walletHistory(KeycloakAuthenticationToken token, WalletHistoryRequest walletHistoryRequest);

    WalletHistoryPageResponse AgentWalletHistory(KeycloakAuthenticationToken token, WalletHistoryRequest walletHistoryRequest);

    List<CustomerBalanceResponse> walletBalance(KeycloakAuthenticationToken token, WalletBalanceRequest walletBalanceRequest);

    TransactionRes walletToAFB(GenericTransactionRequest transactionRequest);

    TransactionRes walletToWallet(GenericTransactionRequest transactionRequest);

    WalletCashInResponse walletCashIn(String uuid, KeycloakAuthenticationToken token, WalletCashInRequest walletCashInRequest);



    WalletCashOutResponse walletCashOut(String uuid, KeycloakAuthenticationToken token, WalletCashOutRequest walletCashOutRequest);

    WalletToWalletResponse walletToWallet(String uuid, KeycloakAuthenticationToken token, WalletToWalletRequest walletToWalletRequest);

    WalletToWalletResponse walletToWalletInternal(String uuid, KeycloakAuthenticationToken token, WalletToWalletRequest walletToWalletRequest);

    List<CustomerBalanceResponse> walletBalanceByLinkedAccountNumber(KeycloakAuthenticationToken token);

    CustomerBalanceResponse walletUsernameByPhoneNumber(KeycloakAuthenticationToken token);

    SendMoneyToNonWalletResponse sendMoneyToNonWallet(String uuid, KeycloakAuthenticationToken token, SendMoneyToNonWalletRequest sendMoneyToNonWalletRequest);

    ProcessPaymentDetailsResponse processPaymentDetails(String uuid, KeycloakAuthenticationToken token, ProcessPaymentDetailsRequest processPaymentDetailsRequest);

    List<CustomerBalanceResponse> getAgentWalletBalance(KeycloakAuthenticationToken token);

    WalletCustomerDetails validateCustomer(String uuid, KeycloakAuthenticationToken token, ValidateCustomerRequest validateCustomerRequest);
}
