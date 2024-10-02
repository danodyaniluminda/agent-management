package com.digibank.agentmanagement.service.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Asset;
import com.digibank.agentmanagement.web.dto.packagemanagement.AssetDto;

import java.util.List;

public interface AssetService {

    Asset find(Long assetId);

    Asset find(String assetId);

    Asset findByName(String name);

    List<AssetDto> getAll();

    AssetDto get(Long assetId);

    AssetDto add(AssetDto assetRequest);

    AssetDto update(Long assetId, AssetDto assetRequest);

    void delete(Long assetId);

    void delete(String assetId);
}
