package com.biapay.agentmanagement.service;

import com.biapay.agentmanagement.domain.AgentDetails;

public interface AgentDetailsService {

    AgentDetails get(Long agentId);

    AgentDetails get(String agentId);

    AgentDetails findByIAmId(String iAmId);

    AgentDetails findActiveAgentsByMobileNumber(String mobileNumber);

    AgentDetails findByMobileNumber(String mobileNumber);
}
