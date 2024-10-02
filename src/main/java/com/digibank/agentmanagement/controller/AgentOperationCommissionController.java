package com.digibank.agentmanagement.controller;

import com.digibank.agentmanagement.exception.DigibankRuntimeException;
import com.digibank.agentmanagement.service.impl.packagemanagement.AgentCommissionService;
import com.digibank.agentmanagement.web.dto.StatusResponseDTO;
import com.digibank.agentmanagement.web.dto.commission.AgentCommissionDetailResponse;
import com.digibank.agentmanagement.web.dto.commission.AgentOperationCommissionDto;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(
    path = "/api-internal/agentOperationCommission",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class AgentOperationCommissionController {
  @Autowired Environment env;

  private final AgentCommissionService operationService;

  @Operation(description = "Get All Agent Operation Commission")
  @GetMapping(value = "")
  public ResponseEntity<?> getAll(@RequestParam("operationId") Optional<Long> operationId) {
    log.info("Getting all Operations");

    StatusResponseDTO<List<AgentOperationCommissionDto>> response = new StatusResponseDTO<>();

    try {
      List<AgentOperationCommissionDto> agentOperationDtos = operationService.getAll(operationId);
      if (agentOperationDtos != null) {
        response.setStatus(env.getProperty("success"));
        response.setMessage(env.getProperty("agentoperations.list.success"));
        response.setData(agentOperationDtos);
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
      } else {
        response.setStatus(env.getProperty("failure"));
        response.setMessage(env.getProperty("agentoperations.list.failure"));
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.PARTIAL_CONTENT);
      }
    } catch (DigibankRuntimeException ex) {
      log.error("Error in get All Agent Operations : " + ex.getMessage());
      response.setStatus(env.getProperty("failure"));
      response.setMessage(ex.getMessage());
      return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.CONFLICT);
    } catch (Exception e) {
      log.error("Error in get All Operations : " + e.getMessage());
      response.setStatus(env.getProperty("failure"));
      response.setMessage(e.getMessage());
      return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.CONFLICT);
    }
  }

  @Operation(description = "Get Agent Operation Commission")
  @GetMapping(value = "/{operationId}")
  public ResponseEntity<?> get(@PathVariable Long operationId) {
    log.info("Getting Operation: {}", operationId);
    if (operationId == null) {
      throw new DigibankRuntimeException("No Data Found with provided value: #value");
    }

    StatusResponseDTO<AgentOperationCommissionDto> response = new StatusResponseDTO<>();
    try {
      AgentOperationCommissionDto agentOperationDto = operationService.get(operationId);
      if (agentOperationDto != null) {
        response.setStatus(env.getProperty("success"));
        response.setMessage("Agent Operations Commissions List Retrieved successfully");
        response.setData(agentOperationDto);
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
      } else {
        response.setStatus(env.getProperty("failure"));
        response.setMessage("No Agent Operation Commissions Related Information");
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.PARTIAL_CONTENT);
      }
    } catch (Exception e) {
      log.error("Error in get Agent Operation By ID : " + e.getMessage());
      response.setStatus(env.getProperty("failure"));
      response.setMessage(e.getMessage());
      return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.CONFLICT);
    }
  }

  @Operation(description = "Create Agent Operation Commission")
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> create(
      @RequestBody @Valid AgentOperationCommissionDto operationRequest) {
    log.info("Add new Operation Commission: {}", operationRequest);
    if (operationRequest == null) {
      throw new DigibankRuntimeException("No Data Found with provided value: #value");
    }

    StatusResponseDTO<AgentOperationCommissionDto> response = new StatusResponseDTO<>();
    try {
      AgentOperationCommissionDto agentOperationDto = operationService.create(operationRequest);

      if (agentOperationDto != null) {
        response.setStatus(env.getProperty("success"));
        response.setMessage(env.getProperty("Added Agent Operations Commission successfully"));
        response.setData(agentOperationDto);
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
      } else {
        response.setStatus(env.getProperty("failure"));
        response.setMessage("Failed to add Agent Operations Commissions");
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.PARTIAL_CONTENT);
      }

    } catch (DigibankRuntimeException ex) {
      log.error("Error in create Agent Operation Commissions: {}", ex);
      response.setStatus(env.getProperty("failure"));
      response.setMessage(ex.getMessage());
      return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.CONFLICT);
    } catch (Exception e) {
      log.error("Error in create Agent Operation Commissions: {}", e);
      response.setStatus(env.getProperty("failure"));
      response.setMessage(e.getMessage());
      return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.CONFLICT);
    }
  }

  @Operation(description = "Update Agent Operation Commission")
  @PutMapping(value = "/{operationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> update(
      @PathVariable Long operationId,
      @RequestBody @Valid AgentOperationCommissionDto operationRequest) {
    log.info("Update Operation: {} with : {}", operationId, operationRequest);
    if (operationId == null || operationRequest == null) {
      throw new DigibankRuntimeException("No Data Found with provided value: #value");
    }
    if (!operationRequest.getAgentOperationId().equals(operationId)) {
      throw new DigibankRuntimeException("No Data Found with provided value: #value");
    }

    StatusResponseDTO<AgentOperationCommissionDto> response = new StatusResponseDTO<>();
    try {
      AgentOperationCommissionDto OperationDto =
          operationService.update(operationId, operationRequest);
      if (OperationDto != null) {
        response.setStatus(env.getProperty("success"));
        response.setMessage("Agent Operations Commissions information updated successfully");
        response.setData(OperationDto);
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
      } else {
        response.setStatus(env.getProperty("failure"));
        response.setMessage("Failed to update Agent Operations Commissions information");
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.PARTIAL_CONTENT);
      }
    } catch (DigibankRuntimeException ex) {
      log.error("Error in update Agent operation : " + ex.getMessage());
      response.setStatus(env.getProperty("failure"));
      response.setMessage(ex.getMessage());
      return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.CONFLICT);
    } catch (Exception e) {
      log.error("Error in update Agent operation : " + e.getMessage());
      response.setStatus(env.getProperty("failure"));
      response.setMessage(e.getMessage());
      return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.CONFLICT);
    }
  }

  @Operation(description = "Create Agent Operation Commission")
  @DeleteMapping(value = "/{operationId}")
  public ResponseEntity<?> delete(@PathVariable Long operationId) {
    log.info("Delete Operation Commission: {}", operationId);
    if (operationId == null) {
      throw new DigibankRuntimeException("No Data Found with provided value: #value");
    }

    StatusResponseDTO<AgentOperationCommissionDto> response = new StatusResponseDTO<>();
    try {
      operationService.delete(operationId);
      response.setStatus(env.getProperty("success"));
      response.setMessage(" Agent Operations Commissions information deleted successfully");
      return new ResponseEntity<>(new Gson().toJson(response), HttpStatus.OK);
    } catch (Exception e) {
      log.error("Error in delete Agent Operation Commission By Id : {}", e);
      response.setStatus(env.getProperty("failure"));
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(new Gson().toJson(response), HttpStatus.CONFLICT);
    }
  }

  @PostMapping(value = "/calculateCommissionByOperation", produces = MediaType.APPLICATION_JSON_VALUE)
  public AgentCommissionDetailResponse getCommissionByOperation(
      @Valid @RequestBody AgentOperationCommissionDto commissionCalculateRequest) {
    return operationService.calculateCommissionByAgentOperation(commissionCalculateRequest);
  }
}
