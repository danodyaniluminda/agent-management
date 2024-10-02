package com.digibank.agentmanagement.web.assembler.packagemanagement;

import com.digibank.agentmanagement.controller.packagemanagement.OperationController;
import com.digibank.agentmanagement.web.dto.packagemanagement.OperationDto;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OperationModelAssembler implements RepresentationModelAssembler<OperationDto, EntityModel<OperationDto>> {

    @Override
    public @NonNull EntityModel<OperationDto> toModel(@NonNull OperationDto operationDto) {
        return EntityModel.of(operationDto,
                linkTo(methodOn(OperationController.class).get(operationDto.getOperationId())).withSelfRel(),
                linkTo(methodOn(OperationController.class).getAll(null)).withRel("operations"));
    }
}
