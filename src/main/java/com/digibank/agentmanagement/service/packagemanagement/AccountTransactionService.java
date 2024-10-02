package com.digibank.agentmanagement.service.packagemanagement;

import com.digibank.agentmanagement.web.dto.accounttransfer.request.*;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.*;
import com.digibank.agentmanagement.web.dto.packagemanagement.AccountTransactionRequest;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

import java.util.List;

public interface AccountTransactionService {

    AccountBalanceResponse getAccountBalance(KeycloakAuthenticationToken token, BalanceInquiryRequest balanceInquiryRequest);

    List<AccountStatementResponse> getAccountStatement(KeycloakAuthenticationToken token, AccountStatementRequest accountStatementRequest);



    CashDepositWithdrawResponse cashDepositToAccount(String uuid, KeycloakAuthenticationToken token, CashDepositRequest cashDepositRequest);

    CashDepositWithdrawResponse cashWithdrawalFromAccount(String uuid, KeycloakAuthenticationToken token, CashWithDrawlRequest cashWithDrawlRequest);

    CustomerDetailsResponse validateCustomer(KeycloakAuthenticationToken token, String customerId);

    List<CustomerAccountsResponse> customerAccounts(KeycloakAuthenticationToken token, String customerId);

    CustomerAccountsResponse getAccountDetails(KeycloakAuthenticationToken token, String accountNumber);


    AccountBalanceResponse getAgentAccountBalance(KeycloakAuthenticationToken token , String accountNumber);

    List<AccountStatementResponse> getAgentAccountStatement(KeycloakAuthenticationToken token, String fromDate, String toDate, Integer pageNumber, Integer size , String accountNumber);

    AccountOpenResponse openAccount(String uuid, KeycloakAuthenticationToken token, AccountOpenRequest accountOpenRequest);
}
