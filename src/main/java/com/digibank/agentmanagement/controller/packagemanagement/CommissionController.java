package com.digibank.agentmanagement.controller.packagemanagement;

import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.service.packagemanagement.CommissionService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.assembler.packagemanagement.CommissionModelAssembler;
import com.digibank.agentmanagement.web.dto.packagemanagement.CommissionDto;
import com.digibank.agentmanagement.web.dto.packagemanagement.requests.GetAgentCommissionRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api-internal/commissions")
public class CommissionController {

    private final CommissionService commissionService;
    private final CommissionModelAssembler commissionModelAssembler;

    @Operation(description = "Get All Commissions")
    @GetMapping(value = "")
    public CollectionModel<EntityModel<CommissionDto>> getAll() {
        log.info("Getting all Commissions");

        List<EntityModel<CommissionDto>> commissions = commissionService.getAll().stream()
                .map(commissionModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(commissions, linkTo(methodOn(CommissionController.class).getAll()).withSelfRel());
    }

    @Operation(description = "Get Commission")
    @GetMapping(value = "/{commissionId}")
    public EntityModel<CommissionDto> get(@PathVariable Long commissionId) {
        log.info("Getting Commission: {}", commissionId);
        if (commissionId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        return commissionModelAssembler.toModel(commissionService.get(commissionId));
    }

    @Operation(description = "Create Commission")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CommissionDto>> create(@RequestBody @Valid CommissionDto commissionRequest) {
        log.info("Add new Commission: {}", commissionRequest);
        if (commissionRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        CommissionDto CommissionDto = commissionService.add(commissionRequest);
        return ResponseEntity.created(linkTo(methodOn(CommissionController.class)
                        .get(CommissionDto.getCommissionId())).toUri())
                .body(commissionModelAssembler.toModel(CommissionDto));
    }

    @Operation(description = "Update Commission")
    @PutMapping(value = "/{commissionId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CommissionDto>> update(@PathVariable Long commissionId,
                                                             @RequestBody @Valid CommissionDto commissionRequest) {
        log.info("Update Commission: {} with : {}", commissionId, commissionRequest);
        if (commissionId == null || commissionRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        if (!commissionRequest.getCommissionId().equals(commissionId)) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        CommissionDto CommissionDto = commissionService.update(commissionId, commissionRequest);
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(methodOn(AgentUserController.class)
                        .get(CommissionDto.getCommissionId())).toUri())
                .body(commissionModelAssembler.toModel(CommissionDto));
    }

    @Operation(description = "Delete Commission")
    @DeleteMapping(value = "/{commissionId}")
    public ResponseEntity<?> delete(@PathVariable Long commissionId) {
        log.info("Delete Commission: {}", commissionId);
        if (commissionId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        commissionService.delete(commissionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Create Commission")
    @GetMapping(value = {"/agentCommission", "/agent-commission"})
    public ResponseEntity<?> getCommissionByAgent(@Valid @RequestBody GetAgentCommissionRequest commissionRequest) {
        log.info("Get Agent Commission: {}", commissionRequest);
        if (commissionRequest.getAgentMobileNumber() == null || commissionRequest.getOperation() == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        commissionService.getCommissionByAgent(commissionRequest.getAgentMobileNumber(), commissionRequest.getOperation());
        return ResponseEntity.noContent().build();
    }
}
