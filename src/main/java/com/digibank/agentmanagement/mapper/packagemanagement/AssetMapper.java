package com.digibank.agentmanagement.mapper.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Asset;
import com.digibank.agentmanagement.web.dto.packagemanagement.AssetDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssetMapper {

    Asset fromAssetDtoToAsset(AssetDto assetDto);

    AssetDto fromAssetToAssetDto(Asset asset);
}