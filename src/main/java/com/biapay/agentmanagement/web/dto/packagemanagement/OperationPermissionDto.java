package com.biapay.agentmanagement.web.dto.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.CommissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OperationPermissionDto {

    private String operationName;
    private CommissionType commissionType;
    private Boolean active;
}
