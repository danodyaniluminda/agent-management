package com.biapay.agentmanagement.repository;

import com.biapay.agentmanagement.domain.AgentLinkingRequest;
import com.biapay.agentmanagement.domain.AgentLinkingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentLinkingRepository extends JpaRepository<AgentLinkingRequest, Long> {

    List<AgentLinkingRequest> findByParentAgentIdAndLinkingStatus(String parentAgentId , AgentLinkingStatus agentLinkingStatus);

    Optional<AgentLinkingRequest> findByChildAgentId(String childId);
}
