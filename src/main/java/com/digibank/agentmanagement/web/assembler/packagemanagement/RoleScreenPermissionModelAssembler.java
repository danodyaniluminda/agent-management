package com.digibank.agentmanagement.web.assembler.packagemanagement;

import com.digibank.agentmanagement.controller.packagemanagement.RoleScreenPermissionController;
import com.digibank.agentmanagement.web.dto.packagemanagement.RoleScreenPermissionDto;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RoleScreenPermissionModelAssembler implements RepresentationModelAssembler<RoleScreenPermissionDto, EntityModel<RoleScreenPermissionDto>> {

    @Override
    public @NonNull EntityModel<RoleScreenPermissionDto> toModel(@NonNull RoleScreenPermissionDto roleScreenPermission) {

        return EntityModel.of(roleScreenPermission,
                linkTo(methodOn(RoleScreenPermissionController.class).get(roleScreenPermission.getRoleScreenPermissionId())).withSelfRel(),
                linkTo(methodOn(RoleScreenPermissionController.class).getAll( null)).withRel("roleScreenPermissions"));
    }
}
