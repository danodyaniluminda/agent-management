package com.digibank.agentmanagement.controller.packagemanagement;

import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.service.packagemanagement.RoleScreenPermissionService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.assembler.packagemanagement.RoleScreenPermissionModelAssembler;
import com.digibank.agentmanagement.web.dto.packagemanagement.RoleScreenPermissionDto;
import com.digibank.agentmanagement.web.dto.packagemanagement.requests.RoleScreenPermissionRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api-internal/roleScreenPermissions")
public class RoleScreenPermissionController {

    private final RoleScreenPermissionService roleScreenPermissionService;
    private final RoleScreenPermissionModelAssembler roleScreenPermissionModelAssembler;

    @Operation(description = "Get All roleScreenPermissions")
    @GetMapping(value = "")
    public CollectionModel<EntityModel<RoleScreenPermissionDto>> getAll(@RequestParam(required = false) Long userRoleId) {
        log.info("Getting all RoleScreenPermissions");

        List<EntityModel<RoleScreenPermissionDto>> roleScreenPermissionDtos = roleScreenPermissionService.getAll(userRoleId).stream()
                .map(roleScreenPermissionModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(roleScreenPermissionDtos, linkTo(methodOn(RoleScreenPermissionController.class).getAll(userRoleId)).withSelfRel());
    }

    @Operation(description = "Get RoleScreenPermission")
    @GetMapping(value = "/{roleScreenPermissionId}")
    public EntityModel<RoleScreenPermissionDto> get(@PathVariable Long roleScreenPermissionId) {
        log.info("Getting RoleScreenPermission: {}", roleScreenPermissionId);
        if (roleScreenPermissionId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        return roleScreenPermissionModelAssembler.toModel(roleScreenPermissionService.get(roleScreenPermissionId));
    }

    @Operation(description = "Create RoleScreenPermission")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<?>> addAll(@RequestBody @Valid RoleScreenPermissionRequest roleScreenPermissionRequest) {
        log.info("Add new RoleScreenPermission: {}", roleScreenPermissionRequest);
        if (roleScreenPermissionRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        roleScreenPermissionService.addAll(roleScreenPermissionRequest);
        return ResponseEntity.created(linkTo(methodOn(RoleScreenPermissionController.class)
                .getAll(roleScreenPermissionRequest.getUserRoleId())).withRel("roleScreenPermissions").toUri()).build();
    }

    @Operation(description = "Update RoleScreenPermission")
    @PutMapping(value = "/{userRoleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<RoleScreenPermissionDto>> updateAll(@PathVariable Long userRoleId,
                                                                          @RequestBody @Valid RoleScreenPermissionRequest roleScreenPermissionRequest) {
        log.info("Update RoleScreenPermissions : {}", roleScreenPermissionRequest);
        if (userRoleId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        if (roleScreenPermissionRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        roleScreenPermissionService.updateAll(roleScreenPermissionRequest);
        return ResponseEntity.created(linkTo(methodOn(RoleScreenPermissionController.class)
                .getAll(roleScreenPermissionRequest.getUserRoleId())).withRel("roleScreenPermissions").toUri()).build();
    }
}
