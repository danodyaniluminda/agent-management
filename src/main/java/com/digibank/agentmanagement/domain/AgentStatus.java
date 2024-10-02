package com.digibank.agentmanagement.domain;

public enum AgentStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING_PIN_VERIFICATION,
    PENDING_PASSWORD_RESET,
    AGENT_LINKING_PENDING,
    AGENT_LINKING_REQUESTED,
    AGENT_LINKING_COMPLETED,
    AGENT_LINKING_REJECTED,
    AGENT_LINKING_ACCEPTED,
    PENDING_KYC_VERIFICATION,
    REJECTED_KYC_VERIFICATION,

}
