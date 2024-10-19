package com.biapay.agentmanagement.web.dto.packagemanagement;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccessInformationDto {

    private Long accessInfoId;
    @NonNull
    private String userName;
    @NonNull
    private String browser;
    private Instant accessDate;
}
