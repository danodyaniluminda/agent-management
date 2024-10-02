package com.digibank.agentmanagement.controller;

import com.digibank.agentmanagement.domain.AgentOperation;
import com.digibank.agentmanagement.exception.DigibankRuntimeException;
import com.digibank.agentmanagement.service.impl.packagemanagement.AgentOperationService;
import com.digibank.agentmanagement.web.dto.AgentOperationDto;
import com.digibank.agentmanagement.web.dto.StatusResponseDTO;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "/api-internal/agentOperations", produces = MediaType.APPLICATION_JSON_VALUE)
public class AgentOperationController {

  private final AgentOperationService operationService;
  @Autowired
  Environment env;

  @Operation(description = "Get All Agent Operations")
  @GetMapping(value = "")
  public ResponseEntity<?> getAll(@RequestParam("operationName") Optional<String> operationName) {
    log.info("Getting all Operations");

    StatusResponseDTO<List<AgentOperation>> response = new StatusResponseDTO<>();

    try {
      List<AgentOperation> agentOperationDtos = operationService.getAll(operationName);
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

  @Operation(description = "Get Agent Operation")
  @GetMapping(value = "/{operationId}")
  public ResponseEntity<?> get(@PathVariable Long operationId) {
    log.info("Getting Operation: {}", operationId);
    if (operationId == null) {
      throw new DigibankRuntimeException("No Data Found with provided value: #value");
    }

    StatusResponseDTO<AgentOperation> response = new StatusResponseDTO<>();
    try {
      AgentOperation agentOperationDto = operationService.get(operationId);
      if (agentOperationDto != null) {
        response.setStatus(env.getProperty("success"));
        response.setMessage(env.getProperty("agentoperations.info.success"));
        response.setData(agentOperationDto);
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
      } else {
        response.setStatus(env.getProperty("failure"));
        response.setMessage(env.getProperty("agentoperations.info.failure"));
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.PARTIAL_CONTENT);
      }
    } catch (Exception e) {
      log.error("Error in get Agent Operation By ID : " + e.getMessage());
      response.setStatus(env.getProperty("failure"));
      response.setMessage(e.getMessage());
      return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.CONFLICT);
    }
  }

  @Operation(description = "Create Agent Operation")
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> create(@RequestBody @Valid AgentOperationDto operationRequest) {
    log.info("Add new Operation: {}", operationRequest);
    if (operationRequest == null) {
      throw new DigibankRuntimeException("No Data Found with provided value: #value");
    }

    StatusResponseDTO<AgentOperation> response = new StatusResponseDTO<>();
    try {
      AgentOperation agentOperationDto = operationService.add(operationRequest);

      if (agentOperationDto != null) {
        response.setStatus(env.getProperty("success"));
        response.setMessage(env.getProperty("agentoperations.create.success"));
        response.setData(agentOperationDto);
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
      } else {
        response.setStatus(env.getProperty("failure"));
        response.setMessage(env.getProperty("agentoperations.create.failure"));
        return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.PARTIAL_CONTENT);
      }

    } catch (DigibankRuntimeException ex) {
      log.error("Error in create Agent Operations: {}", ex);
      response.setStatus(env.getProperty("failure"));
      response.setMessage(ex.getMessage());
      return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.CONFLICT);
    } catch (Exception e) {
      log.error("Error in create Agent Operations: {}", e);
      response.setStatus(env.getProperty("failure"));
      response.setMessage(e.getMessage());
      return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.CONFLICT);
    }
  }

  @Operation(description = "Create Agent Operation")
  @DeleteMapping(value = "/{operationId}")
  public ResponseEntity<?> delete(@PathVariable Long operationId) {
    log.info("Delete Operation: {}", operationId);
    if (operationId == null) {
      throw new DigibankRuntimeException("No Data Found with provided value: #value");
    }

    StatusResponseDTO<AgentOperation> response = new StatusResponseDTO<AgentOperation>();
    try {
      operationService.delete(operationId);
      response.setStatus(env.getProperty("success"));
      response.setMessage(env.getProperty("agentoperations.delete.success"));
      return new ResponseEntity<>(new Gson().toJson(response), HttpStatus.OK);
    } catch (Exception e) {
      log.error("Error in delete Agent Operation By Id : {}", e);
      response.setStatus(env.getProperty("failure"));
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(new Gson().toJson(response), HttpStatus.CONFLICT);
    }
  }
}
