package com.digibank.agentmanagement.web.dto.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.CommissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommissionDto {

    private Long commissionId;
    private CommissionType commissionType;
    private int commissionAmount;
    private double commissionPercentage;
    private Boolean active;
    private Instant createDate;
}
