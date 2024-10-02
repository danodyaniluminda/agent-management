package com.digibank.agentmanagement.web.assembler.packagemanagement;

import com.digibank.agentmanagement.controller.packagemanagement.UserRoleController;
import com.digibank.agentmanagement.web.dto.packagemanagement.UserRoleDto;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserRoleModelAssembler implements RepresentationModelAssembler<UserRoleDto, EntityModel<UserRoleDto>> {

    @Override
    public @NonNull EntityModel<UserRoleDto> toModel(@NonNull UserRoleDto userRole) {

        return EntityModel.of(userRole,
                linkTo(methodOn(UserRoleController.class).get(userRole.getUserRoleId())).withSelfRel(),
                linkTo(methodOn(UserRoleController.class).getAll()).withRel("userRoles"));
    }
}
