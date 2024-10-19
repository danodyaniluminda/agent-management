package com.biapay.agentmanagement.web.dto.wallettransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletHistoryResponse {

    private int walletEventId;
    private Wallet wallet;
    private WalletTransaction walletTransaction;
    private BigDecimal balanceBeforeTransaction;
    private String eventType;
}
