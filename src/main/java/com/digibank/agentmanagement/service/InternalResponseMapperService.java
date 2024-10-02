package com.digibank.agentmanagement.service;


import com.digibank.agentmanagement.web.dto.accounttransfer.response.AccountStatementResponse;
import com.digibank.agentmanagement.web.dto.response.AccountToAccountFTResponse;
import kong.unirest.json.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class InternalResponseMapperService {

    public AccountToAccountFTResponse fromAccountToAccountTransferResponse(JSONObject jsonObject) {
        if(jsonObject == null)
            return null;

        AccountToAccountFTResponse accountToAccountFTResponse = new AccountToAccountFTResponse();


        if(!jsonObject.isNull("eventNo"))
            accountToAccountFTResponse.setEventNo(jsonObject.getString("eventNo"));
        if(!jsonObject.isNull("eventDate"))
            accountToAccountFTResponse.setEventDate(jsonObject.getString("eventDate"));
        if(!jsonObject.isNull("amount"))
            accountToAccountFTResponse.setAmount(jsonObject.getString("amount"));
        if(!jsonObject.isNull("fees"))
            accountToAccountFTResponse.setFees(jsonObject.getString("fees"));
        if(!jsonObject.isNull("status"))
            accountToAccountFTResponse.setStatus(jsonObject.getString("status"));
        if(!jsonObject.isNull("branchCode"))
            accountToAccountFTResponse.setBranchCode(jsonObject.getString("branchCode"));
        if(!jsonObject.isNull("accountNo"))
            accountToAccountFTResponse.setAccountNo(jsonObject.getString("accountNo"));
        if(!jsonObject.isNull("reason"))
            accountToAccountFTResponse.setReason(jsonObject.getString("reason"));
        if(!jsonObject.isNull("opeCode"))
            accountToAccountFTResponse.setOpeCode(jsonObject.getString("opeCode"));
        if(!jsonObject.isNull("opeTitle"))
            accountToAccountFTResponse.setOpeTitle(jsonObject.getString("opeTitle"));
        if(!jsonObject.isNull("custId"))
            accountToAccountFTResponse.setCustId(jsonObject.getString("custId"));
        if(!jsonObject.isNull("custName"))
            accountToAccountFTResponse.setCustName(jsonObject.getString("custName"));
        if(!jsonObject.isNull("recipientName"))
            accountToAccountFTResponse.setRecipientName(jsonObject.getString("recipientName"));
        if(!jsonObject.isNull("recipientAccount"))
            accountToAccountFTResponse.setRecipientAccount(jsonObject.getString("recipientAccount"));
        if(!jsonObject.isNull("trxId"))
            accountToAccountFTResponse.setTrxId(jsonObject.getString("trxId"));
        if(!jsonObject.isNull("eventLikeModel"))
            accountToAccountFTResponse.setEventLikeModel(jsonObject.getString("eventLikeModel"));
        if(!jsonObject.isNull("trxAuthoriseCode"))
            accountToAccountFTResponse.setTrxAuthoriseCode(jsonObject.getString("trxAuthoriseCode"));
        if(!jsonObject.isNull("secretCode"))
            accountToAccountFTResponse.setSecretCode(jsonObject.getString("secretCode"));
        if(!jsonObject.isNull("secretCodeValidity"))
            accountToAccountFTResponse.setSecretCodeValidity(jsonObject.getString("secretCodeValidity"));
        if(!jsonObject.isNull("senderPhoneNo"))
            accountToAccountFTResponse.setSenderPhoneNo(jsonObject.getString("senderPhoneNo"));
        if(!jsonObject.isNull("recipientPhoneNo"))
            accountToAccountFTResponse.setRecipientPhoneNo(jsonObject.getString("recipientPhoneNo"));
        if(!jsonObject.isNull("referenceExterne"))
            accountToAccountFTResponse.setReferenceExterne(jsonObject.getString("referenceExterne"));
        if(!jsonObject.isNull("referenceExterne2"))
            accountToAccountFTResponse.setReferenceExterne2(jsonObject.getString("referenceExterne2"));
        if(!jsonObject.isNull("externalStatus"))
            accountToAccountFTResponse.setExternalStatus(jsonObject.getString("externalStatus"));


        return accountToAccountFTResponse;
    }

    @SneakyThrows
    public List<AccountStatementResponse> fromAccountStatementResponse(String body) {
        
        
        final ObjectMapper mapper = new ObjectMapper();

        List<AccountStatementResponse> accountStatementResponseArray = Arrays.asList(mapper.readValue(body, AccountStatementResponse[].class));
        log.info("accountStatementResponseArray {}" , accountStatementResponseArray);
        return accountStatementResponseArray;

    }
}
