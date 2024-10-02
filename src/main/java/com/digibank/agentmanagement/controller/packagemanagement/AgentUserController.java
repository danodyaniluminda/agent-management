package com.digibank.agentmanagement.controller.packagemanagement;

import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.service.packagemanagement.AgentUserService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.assembler.packagemanagement.AgentUserModelAssembler;
import com.digibank.agentmanagement.web.dto.ValidateAgentUserDto;
import com.digibank.agentmanagement.web.dto.packagemanagement.AgentUserDto;
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
@RequestMapping("/api-internal/agentUsers")
public class AgentUserController {

    private final AgentUserService agentUserService;
    private final AgentUserModelAssembler agentUserModelAssembler;

    @Operation(description = "Get All AgentUsers")
    @GetMapping(value = "")
    public CollectionModel<EntityModel<AgentUserDto>> getAll() {
        log.info("Getting all AgentUsers");

        List<EntityModel<AgentUserDto>> agentUserDtos = agentUserService.getAll().stream()
                .map(agentUserModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(agentUserDtos, linkTo(methodOn(AgentUserController.class).getAll()).withSelfRel());
    }

    @Operation(description = "Get AgentUser")
    @GetMapping(value = "/{agentUserId}")
    public EntityModel<AgentUserDto> get(@PathVariable Long agentUserId) {
        log.info("Getting AgentUser: {}", agentUserId);
        if (agentUserId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        return agentUserModelAssembler.toModel(agentUserService.get(agentUserId));
    }

    @Operation(description = "Create AgentUser")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<AgentUserDto>> create(
            KeycloakAuthenticationToken token, @RequestBody @Valid AgentUserDto agentUserRequest) {
        log.info("Add new AgentUser: {}", agentUserRequest);
        if (agentUserRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        AgentUserDto agentUserDto = agentUserService.add(token, agentUserRequest);
        return ResponseEntity.created(linkTo(methodOn(AgentUserController.class)
                        .get(agentUserDto.getAgentUserId())).toUri())
                .body(agentUserModelAssembler.toModel(agentUserDto));
    }

    @Operation(description = "Update AgentUser")
    @PutMapping(value = "/{agentUserId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<AgentUserDto>> update(KeycloakAuthenticationToken token,
                                                            @PathVariable Long agentUserId,
                                                            @RequestBody @Valid AgentUserDto agentUserRequest) {
        log.info("Update AgentUser: {} with : {}", agentUserId, agentUserRequest);
        if (agentUserId == null || agentUserRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        if (!agentUserRequest.getAgentUserId().equals(agentUserId)) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        AgentUserDto agentUserDto = agentUserService.update(token, agentUserId, agentUserRequest);
        return ResponseEntity.created(linkTo(methodOn(AgentUserController.class)
                        .get(agentUserDto.getAgentUserId())).toUri())
                .body(agentUserModelAssembler.toModel(agentUserDto));
    }

    @Operation(description = "Create AgentUser")
    @DeleteMapping(value = "/{agentUserId}")
    public ResponseEntity<?> delete(@PathVariable Long agentUserId) {
        log.info("Delete AgentUser: {}", agentUserId);
        if (agentUserId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        agentUserService.delete(agentUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Validate AgentUser")
    @PostMapping(value = "/ValidateAgentUser", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<AgentUserDto>> validateUser(
            KeycloakAuthenticationToken token, @RequestBody @Valid ValidateAgentUserDto validateAgentUserDto) {
        log.info("Add new AgentUser: {}", validateAgentUserDto);
        if (validateAgentUserDto == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        AgentUserDto agentUserDto = agentUserService.validateAgentUser(validateAgentUserDto);
        return ResponseEntity.created(linkTo(methodOn(AgentUserController.class)
                        .get(agentUserDto.getAgentUserId())).toUri())
                .body(agentUserModelAssembler.toModel(agentUserDto));
    }
}
