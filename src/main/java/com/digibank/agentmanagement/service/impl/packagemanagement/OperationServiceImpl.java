package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Asset;
import com.digibank.agentmanagement.domain.packagemanagement.Operation;
import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.exception.ObjectAlreadyExistException;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.mapper.packagemanagement.AssetMapper;
import com.digibank.agentmanagement.mapper.packagemanagement.OperationMapper;
import com.digibank.agentmanagement.repository.packagemanagement.OperationRepository;
import com.digibank.agentmanagement.service.packagemanagement.AssetService;
import com.digibank.agentmanagement.service.packagemanagement.OperationService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.packagemanagement.OperationDto;
import com.digibank.agentmanagement.utils.ApiError;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;

    private final AssetService assetService;
    private final AssetMapper assetMapper;

    @Override
    public Operation find(Long operationId) {
        AgentManagementUtils.isValidId(operationId);
        return operationRepository.findById(operationId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public Operation find(String operationId) {
        try {
            return find(Long.parseLong(operationId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public Operation findByName(String name ) {
        return operationRepository.findByName(name )
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public Operation findByNameAndAsset(String name , Asset asset ) {
        return operationRepository.findByNameAndAsset(name , asset)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public OperationDto get(Long operationId) {
        AgentManagementUtils.isValidId(operationId);
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        OperationDto operationDto = operationMapper.fromOperationToOperationDto(operation);
        return operationDto;
    }

    @Override
    public List<OperationDto> getAll(Optional<String> operationName) {
        List<OperationDto> operations = new ArrayList<>();
        List<Operation> operationList;
        if(operationName.isEmpty()){
            operationList = operationRepository.findAll();
        } else {
            operationList = operationRepository.findAllByName(operationName.get());
        }
        operationList.forEach(operation -> {
            OperationDto operationDto = operationMapper.fromOperationToOperationDto(operation);
            operations.add(operationDto);
        });
        return operations;
    }

    @Override
    public OperationDto add(OperationDto operationRequest) {
        Asset asset = assetService.findByName(operationRequest.getAssetName());
        if(operationRepository.findByNameAndAsset(operationRequest.getName(), asset).isPresent()){
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, operationRequest.getName());
        }

        Operation operation = operationMapper.fromOperationDtoToOperation(operationRequest);
        operation.setCreateDate(Instant.now());
        operation.setAsset(asset);
        operationRepository.save(operation);
        return operationMapper.fromOperationToOperationDto(operation);
    }

    @Override
    public OperationDto update(Long operationId, OperationDto operationRequest) {
        Asset asset = assetService.find(operationRequest.getAssetName());
        if(operationRepository.findByNameAndAssetAndOperationIdNot(operationRequest.getName(), asset, operationId).isPresent()){
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, operationRequest.getName());
        }

        Operation operation = find(operationId);
        operation.setName(operationRequest.getName());
        operation.setActive(operationRequest.getActive());
        operation.setAsset(asset);
        operationRepository.save(operation);
        return operationMapper.fromOperationToOperationDto(operation);
    }

    @Override
    public void delete(Long operationId) {
        AgentManagementUtils.isValidId(operationId);
        Operation operation = find(operationId);
        operationRepository.delete(operation);
    }

    @Override
    public void delete(String operationId) {
        try {
            delete(Long.parseLong(operationId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public List<OperationDto> getAllByAssetName(String assetName) {
        List<OperationDto> operations = new ArrayList<>();
        operationRepository.findAllByAssetName(assetName).forEach(operation -> {
            OperationDto operationDto = operationMapper.fromOperationToOperationDto(operation);
            operations.add(operationDto);
        });
        return operations;
    }
}
