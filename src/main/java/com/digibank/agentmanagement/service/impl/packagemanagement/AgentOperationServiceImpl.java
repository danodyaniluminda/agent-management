package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.AgentOperation;
import com.digibank.agentmanagement.exception.DigibankRuntimeException;
import com.digibank.agentmanagement.mapper.packagemanagement.AgentOperationMapper;
import com.digibank.agentmanagement.repository.AgentOperationRepository;
import com.digibank.agentmanagement.web.dto.AgentOperationDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AgentOperationServiceImpl implements AgentOperationService {

  private final AgentOperationRepository operationRepository;
  private final AgentOperationMapper operationMapper;

  @Override
  public AgentOperation find(Long operationId) {
    isValidId(operationId);
    return operationRepository
        .findById(operationId)
        .orElseThrow(() -> new DigibankRuntimeException("No Data Found with provided value: #value"));
  }

  @Override
  public AgentOperation find(String operationId) {
    try {
      return find(Long.parseLong(operationId));
    } catch (NumberFormatException numberFormatException) {
      throw new DigibankRuntimeException("No Data Found with provided value: #value");
    }
  }

  @Override
  public AgentOperation findByName(String name) {
    return operationRepository
        .findByName(name)
        .orElseThrow(() -> new DigibankRuntimeException("No Data Found with provided value: #value"));
  }

  @Override
  public AgentOperation get(Long operationId) {
    isValidId(operationId);
     return operationRepository
            .findById(operationId)
            .orElseThrow(
                () -> new DigibankRuntimeException("No Data Found with provided value: #value"));
  }

  @Override
  public List<AgentOperation> getAll(Optional<String> operationName) {
    List<AgentOperation> operationList;
    if (operationName.isEmpty()) {
      operationList = operationRepository.findAll();
    } else {
      operationList = operationRepository.findAllByName(operationName.get());
    }
    return operationList;
  }

  @Override
  public AgentOperation add(AgentOperationDto operationRequest) {
    AgentOperation operation = operationMapper.fromOperationDtoToOperation(operationRequest);
    operation.setCreateDate(LocalDateTime.now());
    return operationRepository.save(operation);
  }

  @Override
  public AgentOperation update(Long operationId, AgentOperationDto operationRequest) {
    AgentOperation operation = find(operationId);
    operation.setName(operationRequest.getName());
    operation.setActive(operationRequest.getActive());
    return operationRepository.save(operation);
  }

  @Override
  public void delete(Long operationId) {
    isValidId(operationId);
    AgentOperation operation = find(operationId);
    operationRepository.delete(operation);
  }

  @Override
  public void delete(String operationId) {
    try {
      delete(Long.parseLong(operationId));
    } catch (NumberFormatException numberFormatException) {
      throw new DigibankRuntimeException("Input object or parameter must not be null");
    }
  }

  private static void isValidId(Long id) {
    if (id < 0) {
      throw new DigibankRuntimeException("Input object or parameter must not be null");
    }
  }
}
