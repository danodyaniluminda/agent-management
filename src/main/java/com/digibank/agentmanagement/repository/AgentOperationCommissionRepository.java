package com.digibank.agentmanagement.repository;

import com.digibank.agentmanagement.domain.AgentOperation;
import com.digibank.agentmanagement.domain.AgentOperationCommission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentOperationCommissionRepository
    extends JpaRepository<AgentOperationCommission, Long> {

  Optional<AgentOperationCommission> findByAgentOperation(AgentOperation agentOperation);

  Optional<AgentOperationCommission> findFirstByAgentOperation(AgentOperation agentOperation);

  @Query("from AgentOperationCommission dt where dt.agentOperation = :agentOperation and :amount >= dt.lowerBound and :amount <= dt.upperBound")
  Optional<AgentOperationCommission> findFirstByAgentOperationAndAmount(AgentOperation agentOperation, Integer amount);

  List<AgentOperationCommission> findAllByAgentOperation(AgentOperation agentOperation);
}
