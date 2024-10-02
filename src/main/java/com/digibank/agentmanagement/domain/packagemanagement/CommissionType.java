package com.digibank.agentmanagement.domain.packagemanagement;

import java.util.Arrays;
import java.util.List;

public enum CommissionType {
    FIXED, PERCENTAGE, SLAB, FIXED_AND_PERCENTAGE;

    public List<CommissionType> getAll() {
        return Arrays.asList(CommissionType.values().clone());
    }
}
