package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Asset;
import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.exception.ObjectAlreadyExistException;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.mapper.packagemanagement.AssetMapper;
import com.digibank.agentmanagement.mapper.packagemanagement.OperationMapper;
import com.digibank.agentmanagement.repository.packagemanagement.AssetRepository;
import com.digibank.agentmanagement.repository.packagemanagement.OperationRepository;
import com.digibank.agentmanagement.service.packagemanagement.AssetService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.packagemanagement.AssetDto;
import com.digibank.agentmanagement.web.dto.packagemanagement.OperationDto;
import com.digibank.agentmanagement.utils.ApiError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;
    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;

    @Override
    public Asset find(Long assetId) {
        AgentManagementUtils.isValidId(assetId);
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public Asset find(String assetId) {
        try {
            return find(Long.parseLong(assetId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public Asset findByName(String name) {
        return assetRepository.findByName(name)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public AssetDto get(Long assetId) {
        AgentManagementUtils.isValidId(assetId);
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        AssetDto assetDto = assetMapper.fromAssetToAssetDto(asset);
        enrichDtoWithCollections(assetDto, asset);
        return assetDto;
    }

    @Override
    public List<AssetDto> getAll() {
        List<AssetDto> assetDtos = new ArrayList<>();
        assetRepository.findAll().forEach(asset -> {
            AssetDto assetDto = assetMapper.fromAssetToAssetDto(asset);
            assetDtos.add(assetDto);
        });
        return assetDtos;
    }

    @Override
    public AssetDto add(AssetDto assetRequest) {
        if(assetRepository.findByName(assetRequest.getName()).isPresent()){
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, assetRequest.getName());
        }

        Asset asset = assetMapper.fromAssetDtoToAsset(assetRequest);
        asset.setCreateDate(Instant.now());
        assetRepository.save(asset);
        return assetMapper.fromAssetToAssetDto(asset);
    }

    @Override
    public AssetDto update(Long assetId, AssetDto assetRequest) {
        if(assetRepository.findByNameAndAssetIdNot(assetRequest.getName(), assetId).isPresent()){
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, assetRequest.getName());
        }

        Asset asset = find(assetId);
        asset.setName(assetRequest.getName());
        asset.setActive(assetRequest.getActive());
        assetRepository.save(asset);
        return assetMapper.fromAssetToAssetDto(asset);
    }

    @Override
    public void delete(Long assetId) {
        AgentManagementUtils.isValidId(assetId);
        Asset asset = find(assetId);
        assetRepository.delete(asset);
    }

    @Override
    public void delete(String assetId) {
        try {
            delete(Long.parseLong(assetId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    private void enrichDtoWithCollections(AssetDto assetDto, Asset asset) {
        List<OperationDto> operations = new ArrayList<>();
        operationRepository.findByAsset(asset).forEach(operation -> {
            OperationDto operationDto = operationMapper.fromOperationToOperationDto(operation);
            operations.add(operationDto);
        });
        assetDto.setOperations(operations);
    }
}
