package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.AgentOperation;
import com.digibank.agentmanagement.domain.AgentOperationCommission;
import com.digibank.agentmanagement.domain.packagemanagement.CommissionType;
import com.digibank.agentmanagement.exception.DigibankRuntimeException;
import com.digibank.agentmanagement.mapper.packagemanagement.AgentOperationCommissionMapper;
import com.digibank.agentmanagement.repository.AgentOperationCommissionRepository;
import com.digibank.agentmanagement.repository.AgentOperationRepository;
import com.digibank.agentmanagement.repository.packagemanagement.CurrencyRepository;
import com.digibank.agentmanagement.web.dto.commission.AgentCommissionDetailResponse;
import com.digibank.agentmanagement.web.dto.commission.AgentCommissionRequest;
import com.digibank.agentmanagement.web.dto.commission.AgentOperationCommissionDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AgentCommissionService {
  @Autowired private CurrencyRepository currencyRepository;
  @Autowired private AgentOperationRepository agentOperationRepository;
  @Autowired private AgentOperationCommissionMapper agentOperationCommissionMapper;
  @Autowired private AgentOperationCommissionRepository agentOperationCommissionRepository;

  public AgentOperationCommission find(Long operationId) {
    return agentOperationCommissionRepository
        .findById(operationId)
        .orElseThrow(() -> new DigibankRuntimeException("No Data Found with provided value: #value"));
  }

  public AgentOperationCommissionDto create(AgentOperationCommissionDto operationRequest) {
    AgentOperationCommission agentOperationCommission =
        agentOperationCommissionMapper.fromOperationDtoToOperation(operationRequest);
    AgentOperation agentOperation =
        agentOperationRepository
            .findById(operationRequest.getAgentOperationId())
            .orElseThrow(
                () ->
                    new DigibankRuntimeException(
                        "No Agent Operation Found with provided value: #value"));

    agentOperationCommission.setCommissionType(operationRequest.getCommissionType());
    agentOperationCommission.setPercentage(operationRequest.getPercentage());
    agentOperationCommission.setFixedAmount(operationRequest.getFixedAmount());
    agentOperationCommission.setAgentOperation(agentOperation);
    agentOperationCommission.setInsertDate(LocalDateTime.now());
    return agentOperationCommissionMapper.fromOperationToOperationDto(
        agentOperationCommissionRepository.save(agentOperationCommission));
  }

  public AgentOperationCommissionDto update(
      Long operationId, AgentOperationCommissionDto operationRequest) {
    AgentOperationCommission operation = find(operationId);
    AgentOperation agentOperation =
        agentOperationRepository
            .findById(operationRequest.getAgentOperationId())
            .orElseThrow(
                () ->
                    new DigibankRuntimeException(
                        "No Agent Operation Found with provided value: #value"));

    operation.setAgentOperation(agentOperation);
    operation.setCommissionType(operationRequest.getCommissionType());
    operation.setPercentage(operationRequest.getPercentage());
    operation.setFixedAmount(operationRequest.getFixedAmount());
    operation.setActive(operationRequest.getActive());
    return agentOperationCommissionMapper.fromOperationToOperationDto(
        agentOperationCommissionRepository.save(operation));
  }

  public void delete(Long operationId) {
    AgentOperationCommission operation = find(operationId);
    agentOperationCommissionRepository.delete(operation);
  }

  public AgentOperationCommissionDto get(Long operationId) {
    AgentOperationCommission operation =
        agentOperationCommissionRepository
            .findById(operationId)
            .orElseThrow(
                () -> new DigibankRuntimeException("No Data Found with provided value: #value"));
    return agentOperationCommissionMapper.fromOperationToOperationDto(operation);
  }

  public List<AgentOperationCommissionDto> getAll(Optional<Long> operationId) {
    List<AgentOperationCommissionDto> operations = new ArrayList<>();
    List<AgentOperationCommission> operationList;
    if (operationId.isEmpty()) {
      operationList = agentOperationCommissionRepository.findAll();
    } else {
      AgentOperation agentOperation =
          agentOperationRepository
              .findById(operationId.get())
              .orElseThrow(
                  () ->
                      new DigibankRuntimeException(
                          "No Agent Operation Found with provided value: #value"));

      operationList = agentOperationCommissionRepository.findAllByAgentOperation(agentOperation);
    }
    operationList.forEach(
        operation -> {
          AgentOperationCommissionDto operationDto =
              agentOperationCommissionMapper.fromOperationToOperationDto(operation);
          operations.add(operationDto);
        });
    return operations;
  }

  public AgentCommissionDetailResponse calculateCommissionByAgentOperation(
      AgentOperationCommissionDto request) {
    AgentOperation agentOperation =
        agentOperationRepository
            .findById(request.getAgentOperationId())
            .orElseThrow(
                () ->
                    new DigibankRuntimeException(
                        "No Agent Operation Found with provided value: #value"));


//            .orElseThrow(
//                () ->
//                    new DigibankRuntimeException(
//                        "No Agent Operation Commissions Found with provided value: #value"));

    BigDecimal finalAmount = request.getTransactionAmount();
    BigDecimal commissionAmt = BigDecimal.ZERO;
    BigDecimal fixedAmount = BigDecimal.ZERO;
    BigDecimal percentageAmount = BigDecimal.ZERO;
    String commissionType = null;

    Optional<AgentOperationCommission> agentOperationCommissionOptional =
        agentOperationCommissionRepository
            .findFirstByAgentOperationAndAmount(agentOperation, request.getTransactionAmount().intValue());

    if (agentOperationCommissionOptional.isPresent()){
      AgentOperationCommission agentOperationCommission = agentOperationCommissionOptional.get();

      if (agentOperationCommission.getCommissionType().equals(CommissionType.FIXED)) {
        fixedAmount = agentOperationCommission.getFixedAmount();
        commissionAmt = fixedAmount;
        finalAmount = request.getTransactionAmount().add(fixedAmount);
        commissionType = CommissionType.FIXED.toString();
      }
      if (agentOperationCommission.getCommissionType().equals(CommissionType.PERCENTAGE)) {
        percentageAmount =
            (request.getTransactionAmount().multiply(agentOperationCommission.getPercentage()))
                .divide(BigDecimal.valueOf(100), RoundingMode.UP)
                .setScale(2, RoundingMode.UP);
        commissionAmt = percentageAmount;
        finalAmount = request.getTransactionAmount().add(percentageAmount);
        commissionType = CommissionType.PERCENTAGE.toString();
      }
      if (agentOperationCommission.getCommissionType().equals(CommissionType.FIXED_AND_PERCENTAGE)) {
        percentageAmount =
            (request.getTransactionAmount().multiply(agentOperationCommission.getPercentage()))
                .divide(BigDecimal.valueOf(100), RoundingMode.UP)
                .setScale(2, RoundingMode.UP);
        fixedAmount = agentOperationCommission.getFixedAmount();
        commissionAmt = percentageAmount.add(fixedAmount);
        finalAmount = request.getTransactionAmount().add(commissionAmt);

        commissionType = CommissionType.FIXED_AND_PERCENTAGE.toString();
      }
    }

    return AgentCommissionDetailResponse.builder()
        .fixedAmount(fixedAmount)
        .percentageAmount(percentageAmount)
        .commissionType(commissionType)
        .commissionAmount(commissionAmt)
        .finalAmount(finalAmount)
        .build();
  }
}
