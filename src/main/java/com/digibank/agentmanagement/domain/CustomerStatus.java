package com.digibank.agentmanagement.domain;

public enum CustomerStatus {

    ACTIVE,
    PENDING_PIN_VERIFICATION,
    PENDING_PASSWORD_RESET,
    PENDING_APPROVAL,
    APPROVAL_REJECTED,
    INACTIVE,
}
