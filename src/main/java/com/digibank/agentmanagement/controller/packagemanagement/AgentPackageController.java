package com.digibank.agentmanagement.controller.packagemanagement;

import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.service.packagemanagement.AgentPackageService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.assembler.packagemanagement.AgentPackageModelAssembler;
import com.digibank.agentmanagement.web.dto.packagemanagement.AgentPackageDto;
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
@RestController
@AllArgsConstructor
@RequestMapping("/api-internal/agentPackages")
public class AgentPackageController {

    private final AgentPackageService packageService;
    private final AgentPackageModelAssembler packageModelAssembler;

    @Operation(description = "Get All Currencies")
    @GetMapping(value = "")
    public CollectionModel<EntityModel<AgentPackageDto>> getAll() {
        log.info("Getting all AgentPackages");

        List<EntityModel<AgentPackageDto>> agentPackages = packageService.getAll().stream()
                .map(packageModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(agentPackages, linkTo(methodOn(AgentPackageController.class).getAll()).withSelfRel());
    }

    @Operation(description = "Get AgentPackage")
    @GetMapping(value = "/{packageId}")
    public EntityModel<AgentPackageDto> get(@PathVariable Long packageId) {
        log.info("Getting AgentPackage: {}", packageId);
        if (packageId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        return packageModelAssembler.toModel(packageService.get(packageId));
    }

    @Operation(description = "Create AgentPackage")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<AgentPackageDto>> create(@RequestBody @Valid AgentPackageDto packageRequest) {
        log.info("Add new AgentPackage: {}", packageRequest);
        if (packageRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        AgentPackageDto AgentPackageDto = packageService.add(packageRequest);
        return ResponseEntity.created(linkTo(methodOn(AgentPackageController.class)
                        .get(AgentPackageDto.getPackageId())).toUri())
                .body(packageModelAssembler.toModel(AgentPackageDto));
    }

    @Operation(description = "Update AgentPackage")
    @PutMapping(value = "/{packageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<AgentPackageDto>> update(@PathVariable Long packageId,
                                                               @RequestBody @Valid AgentPackageDto packageRequest) {
        log.info("Update AgentPackage: {} with : {}", packageId, packageRequest);
        if (packageId == null || packageRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        if (!packageRequest.getPackageId().equals(packageId)) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        AgentPackageDto AgentPackageDto = packageService.update(packageId, packageRequest);
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(methodOn(AgentUserController.class)
                        .get(AgentPackageDto.getPackageId())).toUri())
                .body(packageModelAssembler.toModel(AgentPackageDto));
    }

    @Operation(description = "Create AgentPackage")
    @DeleteMapping(value = "/{packageId}")
    public ResponseEntity<?> delete(@PathVariable Long packageId) {
        log.info("Delete AgentPackage: {}", packageId);
        if (packageId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        packageService.delete(packageId);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Create AgentPackage")
    @PutMapping(value = "/{packageId}/{status}")
    public ResponseEntity<?> status(@PathVariable Long packageId, @PathVariable Boolean status) {
        log.info("Delete AgentPackage: {}", packageId);
        if (packageId == null || status == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        packageService.updateStatus(packageId, status);
        return ResponseEntity.noContent().build();
    }
}
