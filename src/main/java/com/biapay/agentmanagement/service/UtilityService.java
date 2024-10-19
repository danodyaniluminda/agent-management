package com.biapay.agentmanagement.service;


import com.biapay.agentmanagement.config.ErrorCodes;
import com.biapay.agentmanagement.domain.*;
import com.biapay.agentmanagement.repository.*;
import com.biapay.agentmanagement.repository.packagemanagement.TransactionRepository;
import com.biapay.agentmanagement.web.dto.response.GenericRejectionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

@Service
@Slf4j
public class UtilityService {


    private AgentDetailsRepository agentDetailsRepository ;
    private TransactionRepository transactionRepository;

    public UtilityService(){}

    public UtilityService(AgentDetailsRepository agentDetailsRepository,

                          TransactionRepository transactionRepository) {
        this.agentDetailsRepository = agentDetailsRepository;
        this.transactionRepository = transactionRepository;

    }

    public boolean checkAgentTranasctionPermission(String agentUserName , String transactionType){

//        Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByPhoneNo(agentUserName);
//
//        if (!optionalAgentDetails.isPresent()){
//            throw new BadRequestException("Customer doesn't exist");
//        }
//
//        if (optionalAgentDetails.get().getStatus() != AgentStatus.ACTIVE) {
//            throw new BadRequestException("Invalid customer status");
//        }
//
//        if (transactionType.equals("FUNDTRANSFER")) {
//            AgentRoleRights RoleAuthorization =  optionalAgentDetails.get().getAgentRoleId().getAgentRoleRights();
//            //log.info("Authorization : {}" , RoleAuthorization.getRights_authorization());
//            //log.info("ScreenSetDetails : {}" , RoleAuthorization.getScreenSetDetails());
//            if(RoleAuthorization.getScreenSetDetails().getFundTransfer().toString().equals("Y")){
//
//                log.info("PERMISSION : AGENT ID {} PERMISSION GRANTED FOR {}",optionalAgentDetails.get().getIamId() ,transactionType );
//                return true;
//            }
//            else
//            {
//                log.info("PERMISSION : AGENT ID {} NOT AUTHORIZED  FOR {}",optionalAgentDetails.get().getIamId() ,transactionType);
//                return false;
//            }
//        }
        return false;

    }

    public GenericRejectionResponse setGenericResponse(String errorCode)
    {
        GenericRejectionResponse genericRejectionResponse = new GenericRejectionResponse();
        if(errorCode.equals(ErrorCodes.PROCESSED_OK_CODE)){
            genericRejectionResponse.setMessage(ErrorCodes.PROCESSED_OK_DESCRIPTION);
            genericRejectionResponse.setCode(ErrorCodes.PROCESSED_OK_CODE);
        }
        else if(errorCode.equals(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE)){
            genericRejectionResponse.setMessage(ErrorCodes.MESSAGE_FORMAT_ERROR_DESCRIPTION);
            genericRejectionResponse.setCode(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE);
        }
        else if(errorCode.equals(ErrorCodes.INVALID_CUSTOMER_ERROR_CODE)){
            genericRejectionResponse.setMessage(ErrorCodes.INVALID_CUSTOMER_ERROR_DESCRIPTION);
            genericRejectionResponse.setCode(ErrorCodes.INVALID_CUSTOMER_ERROR_CODE);
        }
        else if(errorCode.equals(ErrorCodes.INVALID_TRANSACTION_ERROR_CODE)){
            genericRejectionResponse.setMessage(ErrorCodes.INVALID_TRANSACTION_ERROR_DESCRIPTION);
            genericRejectionResponse.setCode(ErrorCodes.INVALID_TRANSACTION_ERROR_CODE);
        }
        else if(errorCode.equals(ErrorCodes.INVALID_AGENT_DETAILS_CODE)){
            genericRejectionResponse.setMessage(ErrorCodes.INVALID_AGENT_DETAILS_DESCRIPTION);
            genericRejectionResponse.setCode(ErrorCodes.INVALID_AGENT_DETAILS_CODE);
        } else if(errorCode.equals(ErrorCodes.INVALID_REQUEST_CODE)){
            genericRejectionResponse.setMessage(ErrorCodes.INVALID_REQUEST);
            genericRejectionResponse.setCode(ErrorCodes.INVALID_REQUEST_CODE);
        }



        return genericRejectionResponse ;
    }

    public void addToTransactionLog (String transactionKey, String fromAccount , String toAccount , String transactionType , 
            String senderCustomerId , String receiverCustomerId , String transactionDescription , 
            String hostResponseId , String responseCode , String jsonResponse )
    {
           Transaction transaction = new Transaction();
           transaction.setFromAccount(fromAccount);
           transaction.setToAccount(toAccount);
           transaction.setTransactionDescription(transactionDescription);
           transaction.setTransactionType(transactionType);
           transaction.setTransactionTime(LocalTime.now());
           transaction.setTransactionDate(LocalDate.now());
           transaction.setFromCustomerId(senderCustomerId);
           transaction.setToCustomerId(receiverCustomerId);
           transaction.setHostResponseCode(hostResponseId);
           transaction.setErrorCode(responseCode);
           transaction.setJsonResponse(jsonResponse);
           transaction.setTransactionKey(transactionKey);
           
           transactionRepository.save(transaction);
           
    }
    
    public String generateTransactionKey(){
        Random rnd = new Random();
        int number = rnd.hashCode();
        // this will convert any number sequence into 6 character.
        return number + "";
    }
    

    /*
    This function will debit limit amount from limit profiles of an Agent

     */
//    public String debitCycleAmount(AgentDetails agentDetails , String amountTransaction , String transactionType)
//    {
//        // fetch limit profiles using customer I am id
//
//        Optional<AgentLimitProfile> agentLimitProfileOptional = agentLimitProfileRepository.findByAgentIdAndTransactionType(agentDetails.getIamId() ,transactionType );
//        if(agentLimitProfileOptional == null || agentLimitProfileOptional.get() == null){
//            log.info("There is no limit Profile available with this AgentID : {} and TransactionType : {}" , agentDetails.getIamId() , transactionType);
//            log.info("Fetch generic limit from Profile table.");
//
////            insertLimitsIntoAgentLimitProfile(agentDetails , transactionType);
//            updateLimitProfile(LimitType.DAILY ,agentDetails , transactionType );
//            updateLimitProfile(LimitType.WEEKLY ,agentDetails , transactionType );
//            updateLimitProfile(LimitType.MONTHLY ,agentDetails , transactionType );
//            agentLimitProfileOptional = agentLimitProfileRepository.findByAgentIdAndTransactionType(agentDetails.getIamId() ,transactionType );
//        }
//
//
//        AgentLimitProfile agentLimitProfile = agentLimitProfileOptional.get();
//        // check if today date is not less then dailyCycleDate then reset DailyLimits
//        // as it is a new transaction for the said Date
//        if(LocalDate.now().compareTo(agentLimitProfile.getDailyCycleDate()) != -1 ){
//            // Update / reset Daily Limit
//            updateLimitProfile(LimitType.DAILY  , agentDetails , transactionType);
//        }
//        if(LocalDate.now().compareTo(agentLimitProfile.getWeeklyCycleDate()) != -1 ){
//            // Update/ reset Weekly Limit
//            updateLimitProfile(LimitType.WEEKLY  , agentDetails , transactionType);
//        }
//        if(LocalDate.now().compareTo(agentLimitProfile.getMonthlyCycleDate()) != -1 ){
//            // Update / reset Weekly Limit
//            updateLimitProfile(LimitType.MONTHLY  , agentDetails , transactionType);
//        }
//
//        agentLimitProfileOptional = agentLimitProfileRepository.findByAgentIdAndTransactionType(agentDetails.getIamId() ,transactionType );
//
//        // check and update Daily Limit
//        if( Integer.getInteger(agentLimitProfile.getRemainingDailyLimit()) > Integer.getInteger(amountTransaction) ){
//            agentLimitProfile.setRemainingDailyLimit( (Integer.getInteger(agentLimitProfile.getRemainingDailyLimit()) - Integer.getInteger(amountTransaction)) + ""  );
//        }
//        else
//        {
//            return ErrorCodes.DAILY_LIMIT_EXCEEDED_CODE ;
//        }
//
//        // Check and update Weekly Limit
//
//        if( Integer.getInteger(agentLimitProfile.getRemianingWeeklyLimit()) > Integer.getInteger(amountTransaction) ){
//            agentLimitProfile.setRemianingWeeklyLimit( (Integer.getInteger(agentLimitProfile.getRemianingWeeklyLimit()) - Integer.getInteger(amountTransaction)) + ""  );
//        }
//        else
//        {
//            return ErrorCodes.WEEKLY_LIMIT_EXCEEDED_CODE ;
//        }
//
//
//        // check and update Monthly limit
//
//        if( Integer.getInteger(agentLimitProfile.getRemainingMonthlyLimit()) > Integer.getInteger(amountTransaction) ){
//            agentLimitProfile.setRemainingMonthlyLimit( (Integer.getInteger(agentLimitProfile.getRemainingMonthlyLimit()) - Integer.getInteger(amountTransaction)) + ""  );
//        }
//        else
//        {
//            return ErrorCodes.MONTHLY_LIMIT_EXCEEDED_CODE ;
//        }
//
//        return ErrorCodes.PROCESSED_OK_CODE;
//
//    }

    private void updateLimitProfile(LimitType limitType, AgentDetails agentDetails, String transactionType) {

//        AgentLimitProfile agentLimitProfile =new AgentLimitProfile();
//        agentLimitProfile.setAgentId(agentDetails.getIamId());
//        agentLimitProfile.setTransactionType(transactionType);
//
//        if(LimitType.DAILY.equals(limitType)){
//
//            if(transactionType.equals(TransactionType.WALLET_CASH_IN)) {
//                agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashInLimitDaily());
//            }
//            else if (transactionType.equals(TransactionType.WALLET_CASH_OUT)) {
//                agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashOutLimitDaily());
//            }
//            else if (transactionType.equals(TransactionType.DAPOSIT)) {
//                agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalCreditLimitDaily());
//            }
//            else if (transactionType.equals(TransactionType.WITHDRAWAL)) {
//                agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalDebitLimitDaily());
//            }
//            else if (transactionType.equals(TransactionType.ACCOUNT_TO_ACCOUNT_FT)) {
//                agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitDaily());
//            }
//            else if (transactionType.equals(TransactionType.ACCOUNT_TO_WALLET_FT)) {
//                agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitDaily());
//            }
//            else if (transactionType.equals(TransactionType.WALLET_TO_ACCOUNT_FT)) {
//                agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitDaily());
//            }
//            else if (transactionType.equals(TransactionType.WALLET_TO_WALLET)) {
//                agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitDaily());
//            }
//
//            agentLimitProfile.setRemainingDailyLimit(agentLimitProfile.getTotalDailyLimit());
//            agentLimitProfile.setDailyCycleDate(LocalDate.now());
//
//
//        }
//        else if(LimitType.WEEKLY.equals(limitType)){
//            if(transactionType.equals(TransactionType.WALLET_CASH_IN)) {
//                agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashInLimitWeekly());
//            }
//            else if (transactionType.equals(TransactionType.WALLET_CASH_OUT)) {
//                agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashOutLimitWeekly());
//            }
//            else if (transactionType.equals(TransactionType.DAPOSIT)) {
//                agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalCreditLimitWeekly());
//            }
//            else if (transactionType.equals(TransactionType.WITHDRAWAL)) {
//                agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalDebitLimitWeekly());
//            }
//            else if (transactionType.equals(TransactionType.ACCOUNT_TO_ACCOUNT_FT)) {
//                agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitWeekly());
//            }
//            else if (transactionType.equals(TransactionType.ACCOUNT_TO_WALLET_FT)) {
//                agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitWeekly());
//            }
//            else if (transactionType.equals(TransactionType.WALLET_TO_ACCOUNT_FT)) {
//                agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitWeekly());
//            }
//            else if (transactionType.equals(TransactionType.WALLET_TO_WALLET)) {
//                agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitWeekly());
//            }
//
//            agentLimitProfile.setRemianingWeeklyLimit(agentLimitProfile.getTotalWeeklyLimit());
//            agentLimitProfile.setWeeklyCycleDate(LocalDate.now().plusDays(7));
//
//        }
//        else if(LimitType.MONTHLY.equals(limitType)){
//            if(transactionType.equals(TransactionType.WALLET_CASH_IN)) {
//                agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashInLimitMonthly());
//            }
//            else if (transactionType.equals(TransactionType.WALLET_CASH_OUT)) {
//                agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashOutLimitMonthly());
//            }
//            else if (transactionType.equals(TransactionType.DAPOSIT)) {
//                agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalCreditLimitMonthly());
//            }
//            else if (transactionType.equals(TransactionType.WITHDRAWAL)) {
//                agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalDebitLimitMonthly());
//            }
//            else if (transactionType.equals(TransactionType.ACCOUNT_TO_ACCOUNT_FT)) {
//                agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitMonthly());
//            }
//            else if (transactionType.equals(TransactionType.ACCOUNT_TO_WALLET_FT)) {
//                agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitMonthly());
//            }
//            else if (transactionType.equals(TransactionType.WALLET_TO_ACCOUNT_FT)) {
//                agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitMonthly());
//            }
//            else if (transactionType.equals(TransactionType.WALLET_TO_WALLET)) {
//                agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitMonthly());
//            }
//
//            agentLimitProfile.setRemainingMonthlyLimit(agentLimitProfile.getTotalMonthlyLimit());
//            agentLimitProfile.setMonthlyCycleDate(LocalDate.now().plusDays(30));
//
//        }
//
//        agentLimitProfileRepository.save(agentLimitProfile);
    }

//    public void insertLimitsIntoAgentLimitProfile(AgentDetails agentDetails  , String transactionType){
//
//        AgentLimitProfile agentLimitProfile =new AgentLimitProfile();
//        agentLimitProfile.setAgentId(agentDetails.getIamId());
//        agentLimitProfile.setTransactionType(transactionType);
//        if(transactionType.equals(TransactionType.WALLET_CASH_IN)) {
//            agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashInLimitDaily());
//            agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashInLimitWeekly());
//            agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashInLimitMonthly());
//        }
//        else if (transactionType.equals(TransactionType.WALLET_CASH_OUT)) {
//            agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashOutLimitDaily());
//            agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashOutLimitWeekly());
//            agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getCashOutLimitMonthly());
//        }
//        else if (transactionType.equals(TransactionType.DAPOSIT)) {
//            agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalCreditLimitDaily());
//            agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalCreditLimitWeekly());
//            agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalCreditLimitMonthly());
//        }
//        else if (transactionType.equals(TransactionType.WITHDRAWAL)) {
//            agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalDebitLimitDaily());
//            agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalDebitLimitWeekly());
//            agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getTotalDebitLimitMonthly());
//        }
//        else if (transactionType.equals(TransactionType.ACCOUNT_TO_ACCOUNT_FT)) {
//            agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitDaily());
//            agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitWeekly());
//            agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitMonthly());
//        }
//        else if (transactionType.equals(TransactionType.ACCOUNT_TO_WALLET_FT)) {
//            agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitDaily());
//            agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitWeekly());
//            agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitMonthly());
//        }
//        else if (transactionType.equals(TransactionType.WALLET_TO_ACCOUNT_FT)) {
//            agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitDaily());
//            agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitWeekly());
//            agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitMonthly());
//        }
//        else if (transactionType.equals(TransactionType.WALLET_TO_WALLET)) {
//            agentLimitProfile.setTotalDailyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitDaily());
//            agentLimitProfile.setTotalWeeklyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitWeekly());
//            agentLimitProfile.setTotalMonthlyLimit(agentDetails.getAgentProfileId().getAgentProfileDetail().getFundTransferLimitMonthly());
//        }
//
//        agentLimitProfile.setRemainingDailyLimit(agentLimitProfile.getTotalDailyLimit());
//        agentLimitProfile.setRemianingWeeklyLimit(agentLimitProfile.getTotalWeeklyLimit());
//        agentLimitProfile.setRemainingMonthlyLimit(agentLimitProfile.getTotalMonthlyLimit());
//        agentLimitProfile.setDailyCycleDate(LocalDate.now().plusDays(0));
//        agentLimitProfile.setWeeklyCycleDate(LocalDate.now().plusDays(7));
//        agentLimitProfile.setMonthlyCycleDate(LocalDate.now().plusDays(30));
//
//        agentLimitProfileRepository.save(agentLimitProfile);
//
//    }


}

