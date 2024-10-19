package com.biapay.agentmanagement.web.assembler.packagemanagement;

import com.biapay.agentmanagement.controller.packagemanagement.AccessInformationController;
import com.biapay.agentmanagement.web.dto.packagemanagement.AccessInformationDto;
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
