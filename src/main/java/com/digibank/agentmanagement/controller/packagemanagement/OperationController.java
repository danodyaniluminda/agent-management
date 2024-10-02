package com.digibank.agentmanagement.controller.packagemanagement;

import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.service.packagemanagement.OperationService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.assembler.packagemanagement.OperationModelAssembler;
import com.digibank.agentmanagement.web.dto.packagemanagement.OperationDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api-internal/operations")
public class OperationController {

    private final OperationService operationService;
    private final OperationModelAssembler operationModelAssembler;

    @Operation(description = "Get All Operations")
    @GetMapping(value = "")
    public CollectionModel<EntityModel<OperationDto>> getAll(@RequestParam("operationName") Optional<String> operationName) {
        log.info("Getting all Operations");

        List<EntityModel<OperationDto>> operations = operationService.getAll(operationName).stream()
                .map(operationModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(operations, linkTo(methodOn(OperationController.class).getAll(operationName)).withSelfRel());
    }

    @Operation(description = "Get All Operations by Asset")
    @GetMapping(value = "/asset")
    public CollectionModel<EntityModel<OperationDto>> getAllByAsset(@RequestParam("assetName") String assetName) {
        log.info("Getting all Operations by Asset");
        if (StringUtils.isEmpty(assetName)) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        List<EntityModel<OperationDto>> operations = operationService.getAllByAssetName(assetName).stream()
                .map(operationModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(operations, linkTo(methodOn(OperationController.class).getAllByAsset(assetName)).withSelfRel());
    }

    @Operation(description = "Get Operation")
    @GetMapping(value = "/{operationId}")
    public EntityModel<OperationDto> get(@PathVariable Long operationId) {
        log.info("Getting Operation: {}", operationId);
        if (operationId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        return operationModelAssembler.toModel(operationService.get(operationId));
    }

    @Operation(description = "Create Operation")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<OperationDto>> create(@RequestBody @Valid OperationDto operationRequest) {
        log.info("Add new Operation: {}", operationRequest);
        if (operationRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        OperationDto operationDto = operationService.add(operationRequest);
        return ResponseEntity.created(linkTo(methodOn(OperationController.class)
                        .get(operationDto.getOperationId())).toUri())
                .body(operationModelAssembler.toModel(operationDto));
    }

    @Operation(description = "Update Operation")
    @PutMapping(value = "/{operationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<OperationDto>> walletAccountOpening(@PathVariable Long operationId,
                                                                          @RequestBody @Valid OperationDto operationRequest) {
        log.info("Update Operation: {} with : {}", operationId, operationRequest);
        if (operationId == null || operationRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        if (!operationRequest.getOperationId().equals(operationId)) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        OperationDto OperationDto = operationService.update(operationId, operationRequest);
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(methodOn(AgentUserController.class)
                        .get(OperationDto.getOperationId())).toUri())
                .body(operationModelAssembler.toModel(OperationDto));
    }

    @Operation(description = "Create Operation")
    @DeleteMapping(value = "/{operationId}")
    public ResponseEntity<?> delete(@PathVariable Long operationId) {
        log.info("Delete Operation: {}", operationId);
        if (operationId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        operationService.delete(operationId);
        return ResponseEntity.noContent().build();
    }
}
