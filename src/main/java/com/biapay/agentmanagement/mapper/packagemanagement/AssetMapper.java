package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.Asset;
import com.biapay.agentmanagement.web.dto.packagemanagement.AssetDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssetMapper {

    Asset fromAssetDtoToAsset(AssetDto assetDto);

    AssetDto fromAssetToAssetDto(Asset asset);
}