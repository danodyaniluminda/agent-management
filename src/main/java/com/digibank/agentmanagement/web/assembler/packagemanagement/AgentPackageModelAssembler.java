package com.digibank.agentmanagement.web.assembler.packagemanagement;

import com.digibank.agentmanagement.controller.packagemanagement.AgentPackageController;
import com.digibank.agentmanagement.web.dto.packagemanagement.AgentPackageDto;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AgentPackageModelAssembler implements RepresentationModelAssembler<AgentPackageDto, EntityModel<AgentPackageDto>> {

    @Override
    public @NonNull EntityModel<AgentPackageDto> toModel(@NonNull AgentPackageDto agentPackage) {

        EntityModel<AgentPackageDto> agentPackageDtoEntityModel = EntityModel.of(agentPackage,
                linkTo(methodOn(AgentPackageController.class).get(agentPackage.getPackageId())).withSelfRel(),
                linkTo(methodOn(AgentPackageController.class).getAll()).withRel("agentPackages"));
//        if (agentPackage.getActive()) {
//            agentPackageDtoEntityModel.add(linkTo(methodOn(AgentPackageController.class).status(agentPackage.getPackageId(), Boolean.FALSE)).withSelfRel());
//        } else {
//            agentPackageDtoEntityModel.add(linkTo(methodOn(AgentPackageController.class).status(agentPackage.getPackageId(), Boolean.TRUE)).withSelfRel());
//        }
        return agentPackageDtoEntityModel;
    }
}
