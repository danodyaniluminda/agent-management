package com.digibank.agentmanagement.service.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Asset;
import com.digibank.agentmanagement.domain.packagemanagement.Operation;
import com.digibank.agentmanagement.web.dto.packagemanagement.OperationDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OperationService {

    Operation find(Long operationId);

    Operation find(String operationId);

    Operation findByName(String name);

    Operation findByNameAndAsset(String name , Asset asset);

    List<OperationDto> getAll(Optional<String> operationName);

    OperationDto get(Long operationId);

    OperationDto add(OperationDto operationRequest);

    OperationDto update(Long operationId, OperationDto operationRequest);

    void delete(Long operationId);

    void delete(String operationId);

    List<OperationDto> getAllByAssetName(String assetName);
}
