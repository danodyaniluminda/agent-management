package com.biapay.agentmanagement.web.assembler.packagemanagement;

import com.biapay.agentmanagement.controller.packagemanagement.CommissionController;
import com.biapay.agentmanagement.web.dto.packagemanagement.CommissionDto;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CommissionModelAssembler implements RepresentationModelAssembler<CommissionDto, EntityModel<CommissionDto>> {

    @Override
    public @NonNull EntityModel<CommissionDto> toModel(@NonNull CommissionDto commissionDto) {

        return EntityModel.of(commissionDto,
                linkTo(methodOn(CommissionController.class).get(commissionDto.getCommissionId())).withSelfRel(),
                linkTo(methodOn(CommissionController.class).getAll()).withRel("commissions"));
    }
}
