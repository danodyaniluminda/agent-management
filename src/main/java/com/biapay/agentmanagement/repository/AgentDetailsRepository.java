package com.biapay.agentmanagement.repository;

import com.biapay.agentmanagement.domain.AgentDetails;


import java.util.List;
import java.util.Optional;

import com.biapay.agentmanagement.domain.AgentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AgentDetailsRepository extends JpaRepository<AgentDetails, Long>, JpaSpecificationExecutor<AgentDetails> {

    Optional<AgentDetails> findByAgentEmailAddress(String emailAddress);

    Optional<AgentDetails> findByPhoneNo(String phoneNo);

    Optional<AgentDetails> findByPhoneNoAndStatus(String phoneNo, AgentStatus status);

    Optional<AgentDetails> findByIamId(String loggedInUserName);

    Page<AgentDetails> findAllByStatus(AgentStatus status, Pageable pageable);

    long countBySuperAgentId(String superAgentId);

    List<AgentDetails> findAll(Specification specification);

    List<AgentDetails> findAllBySuperAgentId(String superAgentId);
}
