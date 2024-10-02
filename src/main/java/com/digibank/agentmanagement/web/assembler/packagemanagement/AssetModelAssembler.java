package com.digibank.agentmanagement.web.assembler.packagemanagement;

import com.digibank.agentmanagement.controller.packagemanagement.AssetController;
import com.digibank.agentmanagement.web.dto.packagemanagement.AssetDto;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AssetModelAssembler implements RepresentationModelAssembler<AssetDto, EntityModel<AssetDto>> {

    @Override
    public @NonNull EntityModel<AssetDto> toModel(@NonNull AssetDto assetDto) {

        return EntityModel.of(assetDto,
                linkTo(methodOn(AssetController.class).get(assetDto.getAssetId())).withSelfRel(),
                linkTo(methodOn(AssetController.class).getAll()).withRel("assets"));
    }
}
