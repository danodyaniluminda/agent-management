package com.digibank.agentmanagement.controller.packagemanagement;

import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.service.packagemanagement.AccessInformationService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.assembler.packagemanagement.AccessInformationModelAssembler;
import com.digibank.agentmanagement.web.dto.packagemanagement.AccessInformationDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
@RequestMapping("/api-internal/accessInformations")
public class AccessInformationController {

    private final AccessInformationService accessInformationService;
    private final AccessInformationModelAssembler accessInformationModelAssembler;

    @Operation(description = "Get All Access Information")
    @GetMapping(value = "")
    public CollectionModel<EntityModel<AccessInformationDto>> getAll(@RequestParam(required = false) String userName) {
        log.info("Getting all Access Information");

        List<EntityModel<AccessInformationDto>> accessInformations = accessInformationService.getAll(userName).stream()
                .map(accessInformationModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(accessInformations, linkTo(methodOn(AccessInformationController.class).getAll(userName)).withSelfRel());
    }

    @Operation(description = "Get an Access Information")
    @GetMapping(value = "/{accessInfoId}")
    public EntityModel<AccessInformationDto> get(@PathVariable Long accessInfoId) {
        log.info("Getting UserRole: {}", accessInfoId);
        if (accessInfoId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        return accessInformationModelAssembler.toModel(accessInformationService.get(accessInfoId));
    }

    @Operation(description = "Create Access Information")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<AccessInformationDto>> create(
            KeycloakAuthenticationToken token, @RequestBody @Valid AccessInformationDto accessInformationRequest) {
        log.info("Add new Access Information: {}", accessInformationRequest);
        if (accessInformationRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        AccessInformationDto accessInformationDto = accessInformationService.add(token, accessInformationRequest);
        return ResponseEntity.created(linkTo(methodOn(AccessInformationController.class)
                        .get(accessInformationDto.getAccessInfoId())).toUri())
                .body(accessInformationModelAssembler.toModel(accessInformationDto));
    }
}
