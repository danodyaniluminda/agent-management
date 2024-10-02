package com.digibank.agentmanagement.repository;

import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.AgentLinkingRequest;
import com.digibank.agentmanagement.domain.AgentLinkingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgentLinkingRepository extends JpaRepository<AgentLinkingRequest, Long> {

    List<AgentLinkingRequest> findByParentAgentIdAndLinkingStatus(String parentAgentId , AgentLinkingStatus agentLinkingStatus);

    Optional<AgentLinkingRequest> findByChildAgentId(String childId);
}
