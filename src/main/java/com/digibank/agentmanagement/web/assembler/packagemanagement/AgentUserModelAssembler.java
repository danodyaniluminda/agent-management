package com.digibank.agentmanagement.web.assembler.packagemanagement;

import com.digibank.agentmanagement.controller.packagemanagement.AgentUserController;
import com.digibank.agentmanagement.web.dto.packagemanagement.AgentUserDto;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AgentUserModelAssembler implements RepresentationModelAssembler<AgentUserDto, EntityModel<AgentUserDto>> {

    @Override
    public @NonNull EntityModel<AgentUserDto> toModel(@NonNull AgentUserDto agentUser) {

        return EntityModel.of(agentUser,
                linkTo(methodOn(AgentUserController.class).get(agentUser.getAgentUserId())).withSelfRel(),
                linkTo(methodOn(AgentUserController.class).getAll()).withRel("agentUsers"));
    }
}
