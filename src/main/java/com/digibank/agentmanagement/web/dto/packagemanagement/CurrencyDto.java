package com.digibank.agentmanagement.web.dto.packagemanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CurrencyDto {

    private Long currencyId;
    private String name;
    private String code;
    private String symbol;
    private String rate;
    private Instant createDate;
}
