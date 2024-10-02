package com.digibank.agentmanagement.controller.packagemanagement;

import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.service.packagemanagement.UserRoleService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.assembler.packagemanagement.UserRoleModelAssembler;
import com.digibank.agentmanagement.web.dto.packagemanagement.UserRoleDto;
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
@RequestMapping("/api-internal/userRoles")
public class UserRoleController {

    private final UserRoleService userRoleService;
    private final UserRoleModelAssembler userRoleModelAssembler;

    @Operation(description = "Get All UserRoles")
    @GetMapping(value = "")
    public CollectionModel<EntityModel<UserRoleDto>> getAll() {
        log.info("Getting all AgentUsers");

        List<EntityModel<UserRoleDto>> userRoles = userRoleService.getAll().stream()
                .map(userRoleModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(userRoles, linkTo(methodOn(UserRoleController.class).getAll()).withSelfRel());
    }

    @Operation(description = "Get UserRole")
    @GetMapping(value = "/{userRoleId}")
    public EntityModel<UserRoleDto> get(@PathVariable Long userRoleId) {
        log.info("Getting UserRole: {}", userRoleId);
        if (userRoleId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        return userRoleModelAssembler.toModel(userRoleService.get(userRoleId));
    }

    @Operation(description = "Create UserRole")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<UserRoleDto>> create(
            KeycloakAuthenticationToken token, @RequestBody @Valid UserRoleDto userRoleRequest) {
        log.info("Add new RoleUser: {}", userRoleRequest);
        if (userRoleRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        UserRoleDto userRoleDto = userRoleService.add(token, userRoleRequest);
        return ResponseEntity.created(linkTo(methodOn(UserRoleController.class)
                        .get(userRoleDto.getUserRoleId())).toUri())
                .body(userRoleModelAssembler.toModel(userRoleDto));
    }

    @Operation(description = "Update UserRole")
    @PutMapping(value = "/{userRoleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<UserRoleDto>> walletAccountOpening(
            KeycloakAuthenticationToken token, @PathVariable Long userRoleId,
            @RequestBody @Valid UserRoleDto userRoleRequest) {
        log.info("Update AgentUser: {} with : {}", userRoleId, userRoleRequest);
        if (userRoleId == null || userRoleRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        if (!userRoleRequest.getUserRoleId().equals(userRoleId)) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        UserRoleDto UserRoleDto = userRoleService.update(token, userRoleId, userRoleRequest);
        return ResponseEntity.created(linkTo(methodOn(AgentUserController.class)
                        .get(UserRoleDto.getUserRoleId())).toUri())
                .body(userRoleModelAssembler.toModel(UserRoleDto));
    }

    @Operation(description = "Create UserRole")
    @DeleteMapping(value = "/{userRoleId}")
    public ResponseEntity<?> delete(@PathVariable Long userRoleId) {
        log.info("Delete AgentUser: {}", userRoleId);
        if (userRoleId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        userRoleService.delete(userRoleId);
        return ResponseEntity.noContent().build();
    }
}
