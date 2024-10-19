package com.biapay.agentmanagement.web.assembler.packagemanagement;

import com.biapay.agentmanagement.controller.packagemanagement.CurrencyController;
import com.biapay.agentmanagement.web.dto.packagemanagement.CurrencyDto;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CurrencyModelAssembler implements RepresentationModelAssembler<CurrencyDto, EntityModel<CurrencyDto>> {

    @Override
    public @NonNull EntityModel<CurrencyDto> toModel(@NonNull CurrencyDto currency) {

        return EntityModel.of(currency,
                linkTo(methodOn(CurrencyController.class).get(currency.getCurrencyId())).withSelfRel(),
                linkTo(methodOn(CurrencyController.class).getAll()).withRel("currencies"));
    }
}
