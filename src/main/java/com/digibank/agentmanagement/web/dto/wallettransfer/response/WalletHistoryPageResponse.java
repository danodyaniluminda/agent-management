package com.digibank.agentmanagement.web.dto.wallettransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletHistoryPageResponse {

    private List<WalletHistoryResponse> content;
    private PageInformation pageable;
    private int totalPages;
    private int totalElements;
    private boolean last;
    private boolean first;
    private SortInformation sort;
    private int numberOfElements;
    private int size;
    private int number;
    private boolean empty;
}
