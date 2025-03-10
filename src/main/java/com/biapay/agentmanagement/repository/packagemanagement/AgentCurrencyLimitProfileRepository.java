package com.biapay.agentmanagement.repository.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.AgentLimitProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentCurrencyLimitProfileRepository extends JpaRepository<AgentLimitProfile, Long> {

    Optional<AgentLimitProfile> findByAgentIdAndCurrencyName(Long agentId, String currencyName);
}
