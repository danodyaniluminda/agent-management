package com.digibank.agentmanagement.web.dto.response;


import com.digibank.agentmanagement.web.dto.accounttransfer.response.AccountStatementResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransactionRes {

    AgentDetailsResponse agentDetails;

    BalanceInquiryRespnse balanceInquiryRespnse;

    GenericRejectionResponse genericResponse;

    GenericReceiptDataResponse receiptData;

    AccountToAccountFTResponse accountToAccountFTResponse;

    WalletAccountOpeningResponse walletAccountOpeningResponse;

    List<AccountStatementResponse> accountStatementResponseList;

    CustomerRegistrationResponse customerRegistrationResponse;

}
