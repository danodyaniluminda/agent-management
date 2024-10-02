package com.digibank.agentmanagement.controller.packagemanagement;

import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.service.packagemanagement.AssetService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.assembler.packagemanagement.AssetModelAssembler;
import com.digibank.agentmanagement.web.dto.packagemanagement.AssetDto;
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
@RequestMapping("/api-internal/assets")
public class AssetController {

    private final AssetService assetService;
    private final AssetModelAssembler assetModelAssembler;

    @Operation(description = "Get All Currencies")
    @GetMapping(value = "")
    public CollectionModel<EntityModel<AssetDto>> getAll() {
        log.info("Getting all Assets");

        List<EntityModel<AssetDto>> assets = assetService.getAll().stream()
                .map(assetModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(assets, linkTo(methodOn(AssetController.class).getAll()).withSelfRel());
    }

    @Operation(description = "Get Asset")
    @GetMapping(value = "/{assetId}")
    public EntityModel<AssetDto> get(@PathVariable Long assetId) {
        log.info("Getting Asset: {}", assetId);
        if (assetId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        return assetModelAssembler.toModel(assetService.get(assetId));
    }

    @Operation(description = "Create Asset")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<AssetDto>> create(@RequestBody @Valid AssetDto assetRequest) {
        log.info("Add new Asset: {}", assetRequest);
        if (assetRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        AssetDto AssetDto = assetService.add(assetRequest);
        return ResponseEntity.created(linkTo(methodOn(AssetController.class)
                        .get(AssetDto.getAssetId())).toUri())
                .body(assetModelAssembler.toModel(AssetDto));
    }

    @Operation(description = "Update Asset")
    @PutMapping(value = "/{assetId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<AssetDto>> update(@PathVariable Long assetId,
                                                        @RequestBody @Valid AssetDto assetRequest) {
        log.info("Update Asset: {} with : {}", assetId, assetRequest);
        if (assetId == null || assetRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        if (!assetRequest.getAssetId().equals(assetId)) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        AssetDto AssetDto = assetService.update(assetId, assetRequest);
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(methodOn(AgentUserController.class)
                        .get(AssetDto.getAssetId())).toUri())
                .body(assetModelAssembler.toModel(AssetDto));
    }

    @Operation(description = "Create Asset")
    @DeleteMapping(value = "/{assetId}")
    public ResponseEntity<?> delete(@PathVariable Long assetId) {
        log.info("Delete Asset: {}", assetId);
        if (assetId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        assetService.delete(assetId);
        return ResponseEntity.noContent().build();
    }
}
