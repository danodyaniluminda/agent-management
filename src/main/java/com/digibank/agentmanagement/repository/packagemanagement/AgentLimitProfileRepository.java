package com.digibank.agentmanagement.repository.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.AgentLimitProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentLimitProfileRepository extends JpaRepository<AgentLimitProfile, Long> {

    Optional<AgentLimitProfile> findByAgentIdAndCurrencyName(Long agentId, String currencyName);

    Optional<AgentLimitProfile> findByAgentId(Long agentId);

    List<AgentLimitProfile> deleteByAgentId(Long agentId);
}
