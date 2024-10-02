package com.digibank.agentmanagement.service;


import com.digibank.agentmanagement.config.Constants;
import com.digibank.agentmanagement.config.ErrorCodes;
import com.digibank.agentmanagement.deserializer.responses.ApiResponse;
import com.digibank.agentmanagement.deserializer.responses.ExistingCustomerRegistration;
import com.digibank.agentmanagement.domain.*;
import com.digibank.agentmanagement.exception.BadRequestException;
import com.digibank.agentmanagement.exception.InvalidAuthenticationTokenException;
import com.digibank.agentmanagement.exception.ServiceException;
import com.digibank.agentmanagement.mapper.AgentServiceMapper;
import com.digibank.agentmanagement.repository.*;
import com.digibank.agentmanagement.repository.packagemanagement.TransactionRepository;
import com.digibank.agentmanagement.utils.AgentManagementConstants;
import com.digibank.agentmanagement.utils.AgentManagementPropertyHolder;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.*;
import com.digibank.agentmanagement.web.dto.accounttransfer.request.AccountBillPayRequest;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.AccountBillPayResponse;
import com.digibank.agentmanagement.web.dto.accounttransfer.response.CustomerDetailsResponse;
import com.digibank.agentmanagement.web.dto.common.Bill;
import com.digibank.agentmanagement.web.dto.common.PaymentMerchant;
import com.digibank.agentmanagement.web.dto.common.PaymentService;
import com.digibank.agentmanagement.web.dto.common.ServiceOption;
import com.digibank.agentmanagement.web.dto.response.*;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.dto.usermanagement.response.WalletCustomerDetails;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.*;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.CustomerBalanceResponse;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.PayBillResponse;
import com.google.common.reflect.TypeToken;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionService {


//    private final String bankAdaptorServiceURL;
    private final AgentManagementUtils agentManagementUtils;

    private final DeviceInfoRepository deviceInfoRepository;
    private final AgentDetailsRepository agentDetailsRepository;
    private final AgentServiceMapper agentServiceMapper;
    private final UtilityService utilityService;
    private final BankAdapterService bankAdapterService;
    private final CoreBankAccountRequestsRepository coreBankAccountRequestsRepository;
    private final InternalResponseMapperService internalResponseMapperService ;
    private final NotificationService notificationService;
    private final WalletService walletService;

    private final TransactionRepository transactionRepository;
    private final UserManagementAdapterService userManagementAdapterService;

    private final AgentManagementPropertyHolder agentManagementPropertyHolder;
    private final AgentDetailsService agentDetailsService;
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    public Page<TransactionDto> getLastDayTransactions(int page , int size ){

        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactionPage =  transactionRepository.findAll(pageable);

        List<TransactionDto> transactionDtoList = new ArrayList<TransactionDto>();

        transactionPage.forEach(transaction -> {

            AgentDetails agentDetails = agentDetailsRepository.findByPhoneNo(transaction.getAgentAuthorizationDetail()).get();


            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setTransactionID(transaction.getTransactionKey());
            transactionDto.setTransactionDate(transaction.getTransactionDate());
            transactionDto.setTransactionStatus(transaction.getErrorCode());
            transactionDto.setAmount(transaction.getAmount()+"" );
            transactionDto.setTotalAmount(transaction.getAmount()+"");
            transactionDto.setFee(transaction.getAmount()+"");
            transactionDto.setCurrency(transaction.getCurrencyName());
            transactionDto.setSenderKey(transaction.getFromCustomerId());
            transactionDto.setSenderAccount(transaction.getFromAccount());
            transactionDto.setReceiverKey(transaction.getToCustomerId());
            transactionDto.setReceiverAccount(transaction.getToAccount());
            transactionDto.setUserId(agentDetails.getIamId());
            transactionDto.setUserName(agentDetails.getFullName());
            transactionDto.setUserType(agentDetails.getAgentType()+"");
            transactionDto.setOperation(transaction.getTransactionType());




            transactionDtoList.add(transactionDto);
        });

        //return transactionDtoList.;
        return new PageImpl<>(transactionDtoList, pageable, transactionDtoList.size());

    }

    @Transactional
    public ExistingCustomerRegistration walletAccountOpeningRequest(KeycloakAuthenticationToken token , AccountOpenRequest walletAccountOpeningReq) {
//        if(token == null){
//            throw new InvalidAuthenticationTokenException(ApiError.INVALID_AUTH_TOKEN);
//        }
//        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
//        String agentPhone = loggedInUserName.split("_")[1];
//        log.info("User Name : {}", loggedInUserName);

        return walletService.customerRegistration(walletAccountOpeningReq);
    }

    @Transactional
    public void verifyPin(KeycloakAuthenticationToken token , VerifyPinRequest verifyPinRequest) {
        if(token == null){
            throw new InvalidAuthenticationTokenException(ApiError.INVALID_AUTH_TOKEN);
        }
//        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
//        String agentPhone = loggedInUserName.split("_")[1];
//        log.info("User Name : {}", loggedInUserName);

        walletService.verifyPin(verifyPinRequest);
    }

    @Transactional
    public void resendPin(KeycloakAuthenticationToken token , ResendPinRequest resendPinRequest) {
        if(token == null){
            throw new InvalidAuthenticationTokenException(ApiError.INVALID_AUTH_TOKEN);
        }
//        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
//        String agentPhone = loggedInUserName.split("_")[1];
//        log.info("User Name : {}", loggedInUserName);

        walletService.resendPin(resendPinRequest);
    }

    @Transactional
    public void setPassword(KeycloakAuthenticationToken token , ResetPasswordRequest resetPasswordRequest) {
        if(token == null){
            throw new InvalidAuthenticationTokenException(ApiError.INVALID_AUTH_TOKEN);
        }
//        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
//        String agentPhone = loggedInUserName.split("_")[1];
//        log.info("User Name : {}", loggedInUserName);

        walletService.setPassword(resetPasswordRequest);
    }

//    public TransactionRes accountBalanceRequest(KeycloakAuthenticationToken token , GenericTransactionRequest transactionRequest)
//    {
//        TransactionRes transactionRes = null;
//
//        log.info("inside " + "accountBalanceRequest");
//
//        if(transactionRequest.getAccountNumber() == null)
//        {
//            log.info("Account Not Found.");
//
//            GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
//            genericRejectionResponse.setMessage(ErrorCodes.MESSAGE_FORMAT_ERROR_DESCRIPTION);
//            genericRejectionResponse.setCode(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE);
//            genericRejectionResponse.setCause("ERROR IN INCOMMING MESSAGE;");
//
//            transactionRes = new TransactionRes();
//            transactionRes.setGenericResponse(genericRejectionResponse);
//            return transactionRes;
//        }
//
//        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
//        log.info("User Name : {}", loggedInUserName);
//
//        Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByPhoneNo(loggedInUserName.split("_")[1]);
//        if (optionalAgentDetails.get().getStatus() != AgentStatus.ACTIVE) {
//            log.info("Invalid AGENT STATUS STATUS : {}" , optionalAgentDetails.get().getStatus());
//            throw new BadRequestException("Invalid AGENT STATUS STATUS");
//        }
//
//
//        HttpResponse<String> response = bankAdapterService.getAccountBalance(token , transactionRequest.getAccountNumber());
//
//        GenericRejectionResponse genericRejectionResponse = null;
//        BalanceInquiryRespnse balanceInquiryRespnse = null;
//        GenericReceiptDataResponse genericReceiptDataResponse = null;
//
//        if(response.isSuccess()){
//            balanceInquiryRespnse = fromBalanceInquiryResponse (new JSONObject ( response.getBody() ) ) ;
//            log.info("Response: {}", balanceInquiryRespnse);
//            log.info("Response: {}", balanceInquiryRespnse.getBalance());
//
//            // Setting response Header
//            genericRejectionResponse = new GenericRejectionResponse();
//            genericRejectionResponse.setMessage(ErrorCodes.PROCESSED_OK_DESCRIPTION);
//            genericRejectionResponse.setCode(ErrorCodes.PROCESSED_OK_CODE);
//            genericRejectionResponse.setCause("");
//
//            // setting receipt Data
//
//            genericReceiptDataResponse = agentServiceMapper.setReceiptData(balanceInquiryRespnse);
//        }
//        else {
//            genericRejectionResponse = toGenericRejectionResponse (new JSONObject ( response.getBody() ) ) ;
//            log.info("Response: {}", genericRejectionResponse.getMessage());
//        }
//
//        transactionRes = new TransactionRes();
//        transactionRes.setBalanceInquiryRespnse(balanceInquiryRespnse);
//        transactionRes.setGenericResponse(genericRejectionResponse);
//        transactionRes.setReceiptData(genericReceiptDataResponse);
//        return transactionRes;
//    }

    private GenericRejectionResponse toGenericRejectionResponse(JSONObject jsonObject) {
        if(jsonObject == null)
            return null;

        GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
        genericRejectionResponse.setMessage(jsonObject.getString("message"));
        genericRejectionResponse.setCode(jsonObject.getString("code"));
        genericRejectionResponse.setCause(jsonObject.getString("cause"));

        return genericRejectionResponse;

    }

    private BalanceInquiryRespnse fromBalanceInquiryResponse(JSONObject jsonObject) {
        if(jsonObject == null)
            return null;

        BalanceInquiryRespnse balanceInquiryRespnse = new BalanceInquiryRespnse();

        balanceInquiryRespnse.setBalance(jsonObject.getString("balance"));
        balanceInquiryRespnse.setAccountNumber(jsonObject.getString("accountNumber"));

        return balanceInquiryRespnse;
    }

//    @Transactional
//    public TransactionRes accountToAccountTransfer(KeycloakAuthenticationToken token , GenericTransactionRequest transactionRequest) {
//
//        String phoneNumber = AgentManagementUtils.getLoggedInUserUsername(token);
//        String agentUserName = phoneNumber.split("_")[1]  ;
//        log.info("User Name : {}", agentUserName);
//        TransactionRes transactionRes = null;
//
//
//        // Check Transaction Permission
//
////        if(utilityService.checkAgentTranasctionPermission(agentUserName , "FUNDTRANSFER") == false){
////            GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
////            genericRejectionResponse.setMessage(ErrorCodes.TRANSACTION_PERMISSION_ERROR_DESCRIPTION);
////            genericRejectionResponse.setCode(ErrorCodes.TRANSACTION_PERMISSION_ERROR_CODE);
////            genericRejectionResponse.setCause("{Transaction Permission Module}}");
////
////            transactionRes = new TransactionRes();
////            transactionRes.setGenericResponse(genericRejectionResponse);
////            return transactionRes;
////        }
//
//
//
//        // validation
//        if(transactionRequest.getSender() == null    ||
//            transactionRequest.getReceiver() == null ||
//            transactionRequest.getAmount() == null   ||
//            transactionRequest.getMfaToken() == null ||
//            transactionRequest.getType() == null     )
//        {
//            log.info("Message Format Error.");
//
//            GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
//            genericRejectionResponse.setMessage(ErrorCodes.MESSAGE_FORMAT_ERROR_DESCRIPTION);
//            genericRejectionResponse.setCode(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE);
//            genericRejectionResponse.setCause("ERROR IN INCOMMING MESSAGE;");
//
//            transactionRes = new TransactionRes();
//            transactionRes.setGenericResponse(genericRejectionResponse);
//            return transactionRes;
//        }
//
//
//        log.info("KeyCloak Toeken : {}", ((KeycloakPrincipal) token.getPrincipal())
//                .getKeycloakSecurityContext()
//                .getTokenString());
//
//        HttpResponse<String> response = bankAdapterService.accountToAccountFundTransfer(token , transactionRequest.getSender(),
//                transactionRequest.getReceiver() , transactionRequest.getAmount() , transactionRequest.getDescription(),
//                transactionRequest.getMfaToken() , transactionRequest.getType());
////        return transactionRes;
//
//
//        GenericRejectionResponse genericRejectionResponse = null;
//        AccountToAccountFTResponse accountToAccountFTResponse = null;
//        GenericReceiptDataResponse genericReceiptDataResponse = null;
//
//        if(response.isSuccess()){
//            accountToAccountFTResponse = internalResponseMapperService.fromAccountToAccountTransferResponse (new JSONObject ( response.getBody() ) ) ;
//            log.info("Response: {}", accountToAccountFTResponse);
//
//
//            // Setting response Header
//            genericRejectionResponse = new GenericRejectionResponse();
//            genericRejectionResponse.setMessage(ErrorCodes.PROCESSED_OK_DESCRIPTION);
//            genericRejectionResponse.setCode(ErrorCodes.PROCESSED_OK_CODE);
//            genericRejectionResponse.setCause("");
//
//            // setting receipt Data
//
//            genericReceiptDataResponse = new GenericReceiptDataResponse();
//            genericReceiptDataResponse.setSenderAccount(accountToAccountFTResponse.getAccountNo());
//            genericReceiptDataResponse.setReceiverAccount(accountToAccountFTResponse.getRecipientAccount());
//            genericReceiptDataResponse.setAmountTransactionFee(accountToAccountFTResponse.getFees());
//            genericReceiptDataResponse.setTransactionAmount(accountToAccountFTResponse.getAmount());
//            genericReceiptDataResponse.setReason(accountToAccountFTResponse.getReason());
//            genericReceiptDataResponse.setTransactionStatus(accountToAccountFTResponse.getStatus());
//            genericReceiptDataResponse.setTransactionId(accountToAccountFTResponse.getTrxId());
//            genericReceiptDataResponse.setSenderName(accountToAccountFTResponse.getCustName());
//            genericReceiptDataResponse.setReceiverName(accountToAccountFTResponse.getRecipientName());
//        }
//        else {
//            genericRejectionResponse = toGenericRejectionResponse (new JSONObject ( response.getBody() ) ) ;
//            log.info("Response: {}", genericRejectionResponse.getMessage());
//        }
//
//        transactionRes = new TransactionRes();
//        transactionRes.setAccountToAccountFTResponse(accountToAccountFTResponse);
//        transactionRes.setGenericResponse(genericRejectionResponse);
//        transactionRes.setReceiptData(genericReceiptDataResponse);
//        return transactionRes;
//
//    }


//    @Transactional
//    public TransactionRes accountCashDeposit(KeycloakAuthenticationToken token, GenericTransactionRequest transactionRequest) {
//
//        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
//        log.info("User Name : {}", loggedInUserName);
//        TransactionRes transactionRes = null;
//        // validation
//        if(
//                transactionRequest.getReceiver() == null ||
//                transactionRequest.getAmount() == null   ||
//                transactionRequest.getMfaToken() == null ||
//                transactionRequest.getType() == null     )
//        {
//            log.info("Message Format Error.");
//
//            GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
//            genericRejectionResponse.setMessage(ErrorCodes.MESSAGE_FORMAT_ERROR_DESCRIPTION);
//            genericRejectionResponse.setCode(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE);
//            genericRejectionResponse.setCause("ERROR IN INCOMMING MESSAGE;");
//
//            transactionRes = new TransactionRes();
//            transactionRes.setGenericResponse(genericRejectionResponse);
//            return transactionRes;
//        }
//
//
//        Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByPhoneNo(loggedInUserName.split("_")[1]);
//        if (optionalAgentDetails.get().getStatus() != AgentStatus.ACTIVE && optionalAgentDetails.get().getAgentType() != AgentType.AGENT_BANKER) {
//            log.info("Invalid AGENT STATUS STATUS : {}" , optionalAgentDetails.get().getStatus());
//            throw new BadRequestException("Invalid AGENT STATUS Or Type STATUS");
//        }
//
//        String loggedInUserAccountNumber = optionalAgentDetails.get().getLinkedAccountNumber();
//        log.info("loggedInUserAccountNumber : {}",loggedInUserAccountNumber);
//        transactionRequest.setSender(optionalAgentDetails.get().getLinkedAccountNumber());
//
//        log.info("KeyCloak Toeken : {}", ((KeycloakPrincipal) token.getPrincipal())
//                .getKeycloakSecurityContext()
//                .getTokenString());
//
//        HttpResponse<String> response = bankAdapterService.accountToAccountFundTransfer(token , transactionRequest.getSender(),
//                transactionRequest.getReceiver() , transactionRequest.getAmount() , transactionRequest.getDescription(),
//                transactionRequest.getMfaToken() , transactionRequest.getType());
//
//
//        GenericRejectionResponse genericRejectionResponse = null;
//        AccountToAccountFTResponse accountToAccountFTResponse = null;
//        GenericReceiptDataResponse genericReceiptDataResponse = null;
//
//        if(response.isSuccess()){
//            accountToAccountFTResponse = internalResponseMapperService.fromAccountToAccountTransferResponse (new JSONObject ( response.getBody() ) ) ;
//            log.info("Response: {}", accountToAccountFTResponse);
//
//
//            // Setting response Header
//            genericRejectionResponse = new GenericRejectionResponse();
//            genericRejectionResponse.setMessage(ErrorCodes.PROCESSED_OK_DESCRIPTION);
//            genericRejectionResponse.setCode(ErrorCodes.PROCESSED_OK_CODE);
//            genericRejectionResponse.setCause("");
//
//            // setting receipt Data
//
//            genericReceiptDataResponse = new GenericReceiptDataResponse();
//            genericReceiptDataResponse.setSenderAccount(accountToAccountFTResponse.getAccountNo());
//            genericReceiptDataResponse.setReceiverAccount(accountToAccountFTResponse.getRecipientAccount());
//            genericReceiptDataResponse.setAmountTransactionFee(accountToAccountFTResponse.getFees());
//            genericReceiptDataResponse.setTransactionAmount(accountToAccountFTResponse.getAmount());
//            genericReceiptDataResponse.setReason(accountToAccountFTResponse.getReason());
//            genericReceiptDataResponse.setTransactionStatus(accountToAccountFTResponse.getStatus());
//            genericReceiptDataResponse.setTransactionId(accountToAccountFTResponse.getTrxId());
//            genericReceiptDataResponse.setSenderName(accountToAccountFTResponse.getCustName());
//            genericReceiptDataResponse.setReceiverName(accountToAccountFTResponse.getRecipientName());
//        }
//        else {
//            genericRejectionResponse = toGenericRejectionResponse (new JSONObject ( response.getBody() ) ) ;
//            log.info("Response: {}", genericRejectionResponse.getMessage());
//        }
//
//        transactionRes = new TransactionRes();
//        //transactionRes.setAccountToAccountFTResponse(accountToAccountFTResponse);
//        transactionRes.setGenericResponse(genericRejectionResponse);
//        transactionRes.setReceiptData(genericReceiptDataResponse);
//        return transactionRes;
//    }

//    @Transactional
//    public TransactionRes accountCashWithdrawal(KeycloakAuthenticationToken token, GenericTransactionRequest transactionRequest) {
//
//        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
//        log.info("User Name : {}", loggedInUserName);
//        TransactionRes transactionRes = null;
//        // validation
//        if(
//                transactionRequest.getSender() == null ||
//                        transactionRequest.getAmount() == null   ||
//                        transactionRequest.getMfaToken() == null ||
//                        transactionRequest.getType() == null     )
//        {
//            log.info("Message Format Error.");
//
//            GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
//            genericRejectionResponse.setMessage(ErrorCodes.MESSAGE_FORMAT_ERROR_DESCRIPTION);
//            genericRejectionResponse.setCode(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE);
//            genericRejectionResponse.setCause("ERROR IN INCOMMING MESSAGE;");
//
//            transactionRes = new TransactionRes();
//            transactionRes.setGenericResponse(genericRejectionResponse);
//            return transactionRes;
//        }
//
//
//        Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByPhoneNo(loggedInUserName.split("_")[1]);
//        if (optionalAgentDetails.get().getStatus() != AgentStatus.ACTIVE && optionalAgentDetails.get().getAgentType() != AgentType.AGENT_BANKER) {
//            log.info("Invalid AGENT STATUS STATUS : {}" , optionalAgentDetails.get().getStatus());
//            throw new BadRequestException("Invalid AGENT STATUS Or Type STATUS");
//        }
//
//        String loggedInUserAccountNumber = optionalAgentDetails.get().getLinkedAccountNumber();
//        log.info("loggedInUserAccountNumber : {}",loggedInUserAccountNumber);
//        transactionRequest.setReceiver(optionalAgentDetails.get().getLinkedAccountNumber());
//
//        log.info("KeyCloak Toeken : {}", ((KeycloakPrincipal) token.getPrincipal())
//                .getKeycloakSecurityContext()
//                .getTokenString());
//
//
//        HttpResponse<String> response = bankAdapterService.accountToAccountFundTransfer(token , transactionRequest.getSender(),
//                transactionRequest.getReceiver() , transactionRequest.getAmount() , transactionRequest.getDescription(),
//                transactionRequest.getMfaToken() , transactionRequest.getType());
//
//
//
//        GenericRejectionResponse genericRejectionResponse = null;
//        AccountToAccountFTResponse accountToAccountFTResponse = null;
//        GenericReceiptDataResponse genericReceiptDataResponse = null;
//
//        if(response.isSuccess()){
//            accountToAccountFTResponse = internalResponseMapperService.fromAccountToAccountTransferResponse (new JSONObject ( response.getBody() ) ) ;
//            log.info("Response: {}", accountToAccountFTResponse);
//
//
//            // Setting response Header
//            genericRejectionResponse = new GenericRejectionResponse();
//            genericRejectionResponse.setMessage(ErrorCodes.PROCESSED_OK_DESCRIPTION);
//            genericRejectionResponse.setCode(ErrorCodes.PROCESSED_OK_CODE);
//            genericRejectionResponse.setCause("");
//
//            // setting receipt Data
//
//            genericReceiptDataResponse = new GenericReceiptDataResponse();
//            genericReceiptDataResponse.setSenderAccount(accountToAccountFTResponse.getAccountNo());
//            genericReceiptDataResponse.setReceiverAccount(accountToAccountFTResponse.getRecipientAccount());
//            genericReceiptDataResponse.setAmountTransactionFee(accountToAccountFTResponse.getFees());
//            genericReceiptDataResponse.setTransactionAmount(accountToAccountFTResponse.getAmount());
//            genericReceiptDataResponse.setReason(accountToAccountFTResponse.getReason());
//            genericReceiptDataResponse.setTransactionStatus(accountToAccountFTResponse.getStatus());
//            genericReceiptDataResponse.setTransactionId(accountToAccountFTResponse.getTrxId());
//            genericReceiptDataResponse.setSenderName(accountToAccountFTResponse.getCustName());
//            genericReceiptDataResponse.setReceiverName(accountToAccountFTResponse.getRecipientName());
//        }
//        else {
//            genericRejectionResponse = toGenericRejectionResponse (new JSONObject ( response.getBody() ) ) ;
//            log.info("Response: {}", genericRejectionResponse.getMessage());
//        }
//
//        transactionRes = new TransactionRes();
//        //transactionRes.setAccountToAccountFTResponse(accountToAccountFTResponse);
//        transactionRes.setGenericResponse(genericRejectionResponse);
//        transactionRes.setReceiptData(genericReceiptDataResponse);
//        return transactionRes;
//    }

    @Transactional
    public TransactionRes coreAccountOpeningRequest(KeycloakAuthenticationToken token, AccountOpeningReq accountOpeningReq) {
        TransactionRes transactionRes = new TransactionRes();
        if(token == null){
            log.info("Auth Token Not found");
            transactionRes.setGenericResponse(utilityService.setGenericResponse(ErrorCodes.INVALID_AUTH_TOKEN_CODE));
            return transactionRes;
        }

        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);



        log.info("User Name : {}", loggedInUserName);

        // Insert Customer record if transaction got approved
        CoreBankAccountOpeningRequest coreBankAccountOpeningRequest =agentServiceMapper.fromAccoutnOpeningRequestToEntity (accountOpeningReq);
        coreBankAccountOpeningRequest.setUid(UUID.randomUUID().toString());
        coreBankAccountOpeningRequest.setCreatedBy("SYSTEM");
        coreBankAccountOpeningRequest.setLastModifiedBy("SYSTEM");
        coreBankAccountOpeningRequest.setRegistrationStatus(RegistrationStatus.PENDING);
        coreBankAccountRequestsRepository.save(coreBankAccountOpeningRequest)  ;



        transactionRes.setGenericResponse(utilityService.setGenericResponse(ErrorCodes.PROCESSED_OK_CODE));

        WalletAccountOpeningResponse walletAccountOpeningResponse = new WalletAccountOpeningResponse(coreBankAccountOpeningRequest.getUid() , coreBankAccountOpeningRequest.getFirstName() , coreBankAccountOpeningRequest.getEmailAddress());
        transactionRes.setWalletAccountOpeningResponse(walletAccountOpeningResponse);

        return transactionRes;
    }

//    @Transactional
//    public TransactionRes accountStatement(KeycloakAuthenticationToken token, GenericTransactionRequest transactionRequest) {
//        TransactionRes transactionRes = new TransactionRes();
//        if(token == null){
//            log.info("Auth Token Not found");
//            transactionRes.setGenericResponse(utilityService.setGenericResponse(ErrorCodes.INVALID_AUTH_TOKEN_CODE));
//            return transactionRes;
//        }
//
//
//        if(
//                transactionRequest.getFromDate() == null ||
//                        transactionRequest.getToDate() == null   ||
//                        transactionRequest.getAccountNumber() == null ||
//                        transactionRequest.getPageNumber() == null ||
//                        transactionRequest.getSize() == null     )
//        {
//            log.info("Message Format Error.");
//
//            GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
//            genericRejectionResponse.setMessage(ErrorCodes.MESSAGE_FORMAT_ERROR_DESCRIPTION);
//            genericRejectionResponse.setCode(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE);
//            genericRejectionResponse.setCause("ERROR IN INCOMMING MESSAGE;");
//
//            transactionRes.setGenericResponse(genericRejectionResponse);
//            return transactionRes;
//        }
//
//        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
//        String mobileNumber = loggedInUserName.split("_")[1];
//        HttpResponse<String> response = bankAdapterService.accountStatementRequest(token , transactionRequest.getFromDate(),
//                transactionRequest.getToDate() , transactionRequest.getAccountNumber() , transactionRequest.getPageNumber(),
//                transactionRequest.getSize() );
//
//        GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
//        if(response.isSuccess()){
//            List<AccountStatementResponse> accountStatementResponseList = internalResponseMapperService.fromAccountStatementResponse (response.getBody() ) ;
//
//            //transactionRes.setAccountStatementResponseList(accountStatementResponseList);
//            log.info("accountStatementResponseArray {}" , accountStatementResponseList.get(0));
//            transactionRes.setAccountStatementResponseList(accountStatementResponseList);
//
//            // Setting response Header
//            genericRejectionResponse = new GenericRejectionResponse();
//            genericRejectionResponse.setMessage(ErrorCodes.PROCESSED_OK_DESCRIPTION);
//            genericRejectionResponse.setCode(ErrorCodes.PROCESSED_OK_CODE);
//            genericRejectionResponse.setCause("");
//
//
//        }
//        else {
//            genericRejectionResponse = toGenericRejectionResponse (new JSONObject ( response.getBody() ) ) ;
//            log.info("Response: {}", genericRejectionResponse.getMessage());
//        }
//
//
//        transactionRes.setGenericResponse(genericRejectionResponse);
//        return transactionRes;
//
//
//
//    }

    @Transactional
    public void generateMFAForTransaction(KeycloakAuthenticationToken token, GenericTransactionRequest transactionReq) {
        
        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
        String mobileNumber = loggedInUserName.split("_")[1];
        TransactionRes transactionRes = new TransactionRes();
        
        AgentDetails loggedInAgentDetails = agentDetailsRepository.findByPhoneNo(mobileNumber).get();

//        if(transactionReq.getCustomerType() == null){
//            throw new BadRequestException("INVALID INPUT PARAM [PARAM_NAME : CustomerType]");
//        }

        String customerId = "";
        String name = "";
        String phoneNumber = "";
        String email ="";

        HttpResponse<String> response;

        if(transactionReq.getCustomerType().equals("WALLET")  && transactionReq.getCustomerMobile() != null){
            customerId = transactionReq.getCustomerMobile();
            WalletCustomerDetails walletCustomerDetails = userManagementAdapterService.getCustomerDetailsByPhone(token , customerId);

            name = walletCustomerDetails.getFirstName();
            phoneNumber = walletCustomerDetails.getPhoneNumber();
            email = walletCustomerDetails.getEmailAddress();

            log.info("Sending Wallet Customer Transactional OTP" );
        }
        else if (transactionReq.getCustomerType().equals("BANK") && transactionReq.getCustomerId() != null){
            customerId = transactionReq.getCustomerId();
            response = bankAdapterService.getCustomerDetails(token , customerId);
            if(!response.isSuccess()){
                throw new BadRequestException("INVALID CUSTMER ID");
            }

            JSONObject jsonResponse = new JSONObject(response.getBody());
            name = jsonResponse.getString("name");
            phoneNumber = jsonResponse.getString("phoneNumber");
            email = jsonResponse.getString("email");

            log.info("Sending Bank Customer Transactional OTP" );
        }
        else if (transactionReq.getCustomerType().equals("AGENT")){
            name = loggedInAgentDetails.getFullName();
            phoneNumber = loggedInAgentDetails.getPhoneNo();
            email = loggedInAgentDetails.getAgentEmailAddress();

            log.info("Sending LoggedIn-AGENT Transactional OTP" );
        }
        else {
            throw new BadRequestException("INVALID INPUT PARAM");
        }


        if(transactionReq.getMfaChannel() == null){
            throw new BadRequestException("mfaChannel field is missing");
        }





        if(name == null)
            name = "";
        Boolean isSMS = false;
        Boolean isEmail = false;

        if(transactionReq.getMfaChannel() == MFAChannel.BOTH){
            isSMS = true;
            isEmail = true;
        }
        else if(transactionReq.getMfaChannel() == MFAChannel.SMS) {
            isSMS = true;
            isEmail = false;

        }
        else if(transactionReq.getMfaChannel() == MFAChannel.EMAIL) {
            isSMS = false;
            isEmail = true;
        }

        // END
        notificationService.generateMFACodeAndSendCore(UserType.AGENT.toString() , loggedInAgentDetails.getLocale().toString(),
                email, phoneNumber,
                loggedInAgentDetails.getAgentId() , transactionReq.getMfaChannel(),
                Constants.EmailTemplate.TRANSACTION_MFA_REQUEST, Constants.SMSTemplate.TRANSACTION_MFA_REQUEST, 
                Map.of("name", name), false, isEmail, isSMS);




    }
    @Transactional
    public List<AgentDetailsResponse> getAgentList() {
        
        List<AgentDetails> agentDetailsList = agentDetailsRepository.findAll();
        
        List<AgentDetailsResponse> agentDetailsResponses = agentServiceMapper.fromAgentDetailsToAgentDetailsResponses(agentDetailsList);
        
        return agentDetailsResponses;
    }

    @Transactional
    public TransactionRes walletAccountStatement(KeycloakAuthenticationToken token, GenericTransactionRequest transactionRequest) {
        
        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
        String loggedInUserWalletId = loggedInUserName.split("_")[1];
        
        // Using Auth-Token to get Statement
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Transactional
    public TransactionRes walletAccountBalance(KeycloakAuthenticationToken token, GenericTransactionRequest transactionRequest) {
        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
        String loggedInUserWalletId = loggedInUserName.split("_")[1];
        
        // Using Auth-Token to get Balance
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Transactional
    public TransactionRes walletToAFB(KeycloakAuthenticationToken token, GenericTransactionRequest transactionRequest) {
        
        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
        String loggedInUserWalletId = loggedInUserName.split("_")[1];
        
        TransactionRes transactionRes = new TransactionRes();
        if(
                transactionRequest.getAccountNumber()== null ||
                        transactionRequest.getBeneficiaryName()== null   ||
                        transactionRequest.getAmount()== null   ||
                        transactionRequest.getCurrencyCode()== null   ||
                        transactionRequest.getReason()== null ||
                        transactionRequest.getMfaToken()== null )
        {
            log.info("Message Format Error.");

            GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
            genericRejectionResponse.setMessage(ErrorCodes.MESSAGE_FORMAT_ERROR_DESCRIPTION);
            genericRejectionResponse.setCode(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE);
            genericRejectionResponse.setCause("ERROR IN INCOMMING MESSAGE;");

            transactionRes.setGenericResponse(genericRejectionResponse);
            return transactionRes;
        }
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Transactional
    public TransactionRes walletToWallet(KeycloakAuthenticationToken token, GenericTransactionRequest transactionRequest) {
        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
        String loggedInUserWalletId = loggedInUserName.split("_")[1];
        
        
        TransactionRes transactionRes = new TransactionRes();
        if(
                transactionRequest.getBeneficiaryWalletId()== null ||
                        transactionRequest.getAmount()== null   ||
                        transactionRequest.getCurrencyCode()== null   ||
                        transactionRequest.getReason()== null ||
                        transactionRequest.getMfaToken()== null )
        {
            log.info("Message Format Error.");

            GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
            genericRejectionResponse.setMessage(ErrorCodes.MESSAGE_FORMAT_ERROR_DESCRIPTION);
            genericRejectionResponse.setCode(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE);
            genericRejectionResponse.setCause("ERROR IN INCOMMING MESSAGE;");

            transactionRes.setGenericResponse(genericRejectionResponse);
            return transactionRes;
        }
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Transactional
    public TransactionRes walletCashIn(KeycloakAuthenticationToken token, GenericTransactionRequest transactionRequest) {
        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
        String loggedInUserWalletId = loggedInUserName.split("_")[1];
        
        TransactionRes transactionRes = new TransactionRes();
        if(
                transactionRequest.getAccountNumber()== null ||
                        transactionRequest.getAmount()== null   ||
                        transactionRequest.getReason()== null ||
                        transactionRequest.getMfaToken()== null )
        {
            log.info("Message Format Error.");

            GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
            genericRejectionResponse.setMessage(ErrorCodes.MESSAGE_FORMAT_ERROR_DESCRIPTION);
            genericRejectionResponse.setCode(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE);
            genericRejectionResponse.setCause("ERROR IN INCOMMING MESSAGE;");

            transactionRes.setGenericResponse(genericRejectionResponse);
            return transactionRes;
        }
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Transactional
    public TransactionRes walletCashOut(KeycloakAuthenticationToken token, GenericTransactionRequest transactionRequest) {
        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
        String loggedInUserWalletId = loggedInUserName.split("_")[1];
        
        
        TransactionRes transactionRes = new TransactionRes();
        if(
                transactionRequest.getToAccountNumber()== null ||
                        transactionRequest.getAmount()== null   ||
                        transactionRequest.getReason()== null ||
                        transactionRequest.getMfaToken()== null )
        {
            log.info("Message Format Error.");

            GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
            genericRejectionResponse.setMessage(ErrorCodes.MESSAGE_FORMAT_ERROR_DESCRIPTION);
            genericRejectionResponse.setCode(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE);
            genericRejectionResponse.setCause("ERROR IN INCOMMING MESSAGE;");

            transactionRes.setGenericResponse(genericRejectionResponse);
            return transactionRes;
        }
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void saveTransactionLog(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public List<PaymentService> getServices(KeycloakAuthenticationToken token) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "api/pay/services")
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
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "api/pay/merchant")
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

    public List<Bill> getBills(KeycloakAuthenticationToken token, String merchantCode, String serviceNumber, String serviceId) {
        HttpResponse<String> response =
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "api/pay/option")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .queryString("merchantCode", merchantCode)
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
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "api/pay/voucher")
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
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "api/pay/product")
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
                Unirest.get(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "api/pay/subscription")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .queryString("merchantCode", merchantCode)
                        .queryString("serviceNumber", serviceNumber)
                        .queryString("serviceId", serviceId)
                        .asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), new TypeToken<ArrayList<ServiceOption>>() {}.getType());
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }

    public AccountBillPayResponse payBill(KeycloakAuthenticationToken token, String uuid, AccountBillPayRequest accountBillPayRequest) {
        log.info("{} - Calling Backend: {}", uuid, accountBillPayRequest);
        HttpResponse<String> response =
                Unirest.post(agentManagementPropertyHolder.getBankAdaptorServiceURL() + "api-internal/pay")
                        .header(AgentManagementConstants.AUTHORIZATION_HEADER, agentManagementUtils.getBearerTokenString(token))
                        .header("Content-Type", "application/json")
                        .body(AccountBillPayServiceRequest.builder()
                                .amountLocalCur(accountBillPayRequest.getAmount())
                                .payItemId(accountBillPayRequest.getPayItemId())
                                .mfaToken(accountBillPayRequest.getMfaToken())
                                .bankAc(accountBillPayRequest.getBankAc())
                                .paymentCategory(accountBillPayRequest.getPaymentCategory())
                                .serviceNumber(accountBillPayRequest.getServiceNumber())
                                .phoneNumber(accountBillPayRequest.getPhoneNumber())
                                .email(accountBillPayRequest.getEmail())
                                .build()).asString();
        log.info("Response: {}", response.getBody());
        if (response.isSuccess()) {
            return agentManagementUtils.getGson().fromJson(response.getBody(), AccountBillPayResponse.class);
        } else {
            ApiResponse apiResponse = agentManagementUtils.getGson().fromJson(response.getBody(), ApiResponse.class);
            throw new ServiceException(apiResponse);
        }
    }
    @Transactional
    public List<AgentDetailsResponse> getAgentMemberList(KeycloakAuthenticationToken token) {

        String loggedInUserMobileNumber = AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token);
        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(loggedInUserMobileNumber);
        List<AgentDetails> agentDetailsList = agentDetailsRepository.findAllBySuperAgentId(agentDetails.getIamId());
        List<AgentDetailsResponse> agentDetailsResponses = agentServiceMapper.fromAgentDetailsToAgentDetailsResponses(agentDetailsList);

        return agentDetailsResponses;
    }
    @Transactional
    public void changeAgentMemberStatus(KeycloakAuthenticationToken token, String id, String status) {

        if(status.equals(AgentStatus.ACTIVE.toString()) || status.equals(AgentStatus.INACTIVE.toString())) {

            String loggedInUserMobileNumber = AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token);
            AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(loggedInUserMobileNumber);

            AgentDetails agentMember = agentDetailsRepository.findByIamId(id).get();
            if(agentMember.getAgentType() != AgentType.AGENT_MEMBER){
                throw new BadRequestException("INVALID AGENT TYPE");
            }

            if(status.equals(AgentStatus.ACTIVE.toString())){
                agentMember.setStatus(AgentStatus.ACTIVE);
            }
            else if(status.equals(AgentStatus.INACTIVE.toString())){
                agentMember.setStatus(AgentStatus.ACTIVE);
            }

            agentDetailsRepository.save(agentMember);

        }
        else{
            throw new BadRequestException("INVALID STATUS");
        }



    }

    @Builder(toBuilder = true)
    public static class AccountBillPayServiceRequest {
        private BigDecimal amountLocalCur;
        private String payItemId;
        private String mfaToken;
        private String bankAc;
        private String paymentCategory;
        private String serviceNumber;
        private String phoneNumber;
        private String email;
    }
}
