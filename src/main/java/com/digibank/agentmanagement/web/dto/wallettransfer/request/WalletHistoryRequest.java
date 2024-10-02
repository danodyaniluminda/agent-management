package com.digibank.agentmanagement.web.dto.wallettransfer.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletHistoryRequest {

    @NonNull
    private String currencyCodes;
    @NonNull
    private String fromDate;
    @NonNull
    private String toDate;
    @NonNull
    private int page;
    @NonNull
    private int size;


    @NonNull
    private String mobileNumber;
    @NonNull
    private Boolean sendViaEmail;
    @NonNull
    private String mfaToken;

    private String userType;

    private String userId;
}
