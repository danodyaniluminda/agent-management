package com.biapay.agentmanagement.web.assembler.packagemanagement;

import com.biapay.agentmanagement.controller.packagemanagement.ScreenController;
import com.biapay.agentmanagement.web.dto.packagemanagement.ScreenDto;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ScreenModelAssembler implements RepresentationModelAssembler<ScreenDto, EntityModel<ScreenDto>> {

    @Override
    public @NonNull EntityModel<ScreenDto> toModel(@NonNull ScreenDto screenDto) {

        return EntityModel.of(screenDto,
                linkTo(methodOn(ScreenController.class).get(screenDto.getScreenId())).withSelfRel(),
                linkTo(methodOn(ScreenController.class).getAll()).withRel("screens"));
    }
}
