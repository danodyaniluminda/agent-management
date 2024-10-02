package com.digibank.agentmanagement.repository;

import com.digibank.agentmanagement.domain.AgentOperation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentOperationRepository extends JpaRepository<AgentOperation, Long> {

  Optional<AgentOperation> findByName(String name);

  List<AgentOperation> findAllByName(String name);
}
