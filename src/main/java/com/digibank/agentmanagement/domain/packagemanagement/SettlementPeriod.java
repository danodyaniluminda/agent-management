package com.digibank.agentmanagement.domain.packagemanagement;

import java.util.Arrays;
import java.util.List;

public enum SettlementPeriod {
    DAILY, REAL_TIME, NEXT_DAY;

    public List<SettlementPeriod> getAll() {
        return Arrays.asList(SettlementPeriod.values().clone());
    }
}
