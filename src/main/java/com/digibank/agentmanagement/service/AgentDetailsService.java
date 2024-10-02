package com.digibank.agentmanagement.service;

import com.digibank.agentmanagement.domain.AgentDetails;

public interface AgentDetailsService {

    AgentDetails get(Long agentId);

    AgentDetails get(String agentId);

    AgentDetails findByIAmId(String iAmId);

    AgentDetails findActiveAgentsByMobileNumber(String mobileNumber);

    AgentDetails findByMobileNumber(String mobileNumber);
}
