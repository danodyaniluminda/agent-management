package com.digibank.agentmanagement.domain;

import java.util.Arrays;
import java.util.List;


public enum AgentType {
    SUPER_AGENT,
    AGENT_BANKER,
    AGENT_MEMBER,
    AGENT;

    public List<AgentType> getAll() {
        return Arrays.asList(AgentType.values().clone());
    }
}
