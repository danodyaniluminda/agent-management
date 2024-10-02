package com.digibank.agentmanagement.web.assembler.packagemanagement;

import com.digibank.agentmanagement.controller.packagemanagement.AccessInformationController;
import com.digibank.agentmanagement.web.dto.packagemanagement.AccessInformationDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccessInformationModelAssembler implements RepresentationModelAssembler<AccessInformationDto, EntityModel<AccessInformationDto>> {

    @Override
    public EntityModel<AccessInformationDto> toModel(AccessInformationDto accessInformation) {

        return EntityModel.of(accessInformation,
                linkTo(methodOn(AccessInformationController.class).get(accessInformation.getAccessInfoId())).withSelfRel(),
                linkTo(methodOn(AccessInformationController.class).getAll( null)).withRel("accessInformations"));
    }
}
