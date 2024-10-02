package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.AgentOperation;
import com.digibank.agentmanagement.web.dto.AgentOperationDto;
import java.util.List;
import java.util.Optional;

public interface AgentOperationService {

  AgentOperation find(Long operationId);

  AgentOperation find(String operationId);

  AgentOperation findByName(String name);

  List<AgentOperation> getAll(Optional<String> operationName);

  AgentOperation get(Long operationId);

  AgentOperation add(AgentOperationDto operationRequest);

  AgentOperation update(Long operationId, AgentOperationDto operationRequest);

  void delete(Long operationId);

  void delete(String operationId);
}
