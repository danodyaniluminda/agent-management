package com.biapay.agentmanagement.web.dto.wallettransfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SortInformation {

    private boolean sorted;
    private boolean unsorted;
    private boolean empty;
}
