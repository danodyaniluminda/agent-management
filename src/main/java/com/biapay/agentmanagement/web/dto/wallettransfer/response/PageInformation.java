package com.biapay.agentmanagement.web.dto.wallettransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PageInformation {

    private SortInformation sort;
    private int pageNumber;
    private int pageSize;
    private int offset;
    private boolean paged;
    private boolean unpaged;
}
