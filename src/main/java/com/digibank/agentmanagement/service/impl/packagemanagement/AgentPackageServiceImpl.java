package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.*;
import com.digibank.agentmanagement.exception.*;
import com.digibank.agentmanagement.mapper.packagemanagement.AgentPackageMapper;
import com.digibank.agentmanagement.mapper.packagemanagement.CommissionMapper;
import com.digibank.agentmanagement.repository.AgentPackageRepository;
import com.digibank.agentmanagement.service.packagemanagement.*;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.packagemanagement.AgentPackageDto;
import com.digibank.agentmanagement.utils.ApiError;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class AgentPackageServiceImpl implements AgentPackageService {

    private final AgentPackageRepository agentPackageRepository;
    private final AgentPackageMapper agentPackageMapper;

    private final PackageOperationPermissionService packageOperationPermissionService;
    private final PackageCurrencyLimitService packageCurrencyLimitService;
    private final CommissionService commissionService;
    private final AssetService assetService;
    private final OperationService operationService;

    private final CommissionMapper commissionMapper;

    @Override
    public AgentPackage find(Long packageId) {
        AgentManagementUtils.isValidId(packageId);
        return agentPackageRepository.findById(packageId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public AgentPackage find(String packageId) {
        try {
            return find(Long.parseLong(packageId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public AgentPackage findByName(String name) {
        return agentPackageRepository.findByName(name)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public AgentPackageDto get(Long packageId) {
        AgentManagementUtils.isValidId(packageId);
        AgentPackage agentPackage = agentPackageRepository.findById(packageId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        AgentPackageDto agentPackageDto = agentPackageMapper.fromAgentPackageToAgentPackageDto(agentPackage);
        enrichDtoWithCollections(agentPackageDto, agentPackage);
        return agentPackageDto;
    }

    @Override
    public List<AgentPackageDto> getAll() {
        List<AgentPackageDto> agentPackages = new ArrayList<>();
        agentPackageRepository.findAll().forEach(agentPackage -> {
            AgentPackageDto agentPackageDto = agentPackageMapper.fromAgentPackageToAgentPackageDto(agentPackage);
            agentPackages.add(agentPackageDto);
        });
        return agentPackages;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AgentPackageDto add(AgentPackageDto agentPackageRequest) {
//        if (agentPackageRepository.findByName(agentPackageRequest.getName()).isPresent()) {
//            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, agentPackageRequest.getName());
//        }

        try {
            AgentPackage agentPackage = agentPackageMapper.fromAgentPackageDtoToAgentPackage(agentPackageRequest);
            agentPackage.setCreateDate(Instant.now());
            agentPackageRepository.save(agentPackage);

            packageOperationPermissionService.addAll(agentPackage, agentPackageRequest.getOperationPermissionProfiles());
            packageCurrencyLimitService.addAll(agentPackage, agentPackageRequest.getCurrencyLimitProfiles());

            return agentPackageMapper.fromAgentPackageToAgentPackageDto(agentPackage);
        } catch (Exception ex) {
            throw new ObjectCreationFailedException(ApiError.REQUEST_FORMAT_ERROR, agentPackageRequest.getName());
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AgentPackageDto update(Long packageId, AgentPackageDto agentPackageRequest) {

        if (agentPackageRepository.findByNameAndPackageIdNot(agentPackageRequest.getName(), agentPackageRequest.getPackageId()).isPresent()) {
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, agentPackageRequest.getName());
        }

        try {
            AgentPackage agentPackage = find(agentPackageRequest.getPackageId());
            agentPackage.setPackageType(agentPackageRequest.getPackageType());
            agentPackage.setAgentType(agentPackageRequest.getAgentType());
            agentPackage.setChannel(agentPackageRequest.getChannel());
            agentPackage.setIsDefault(agentPackageRequest.getIsDefault());
            agentPackage.setIsFeatured(agentPackageRequest.getIsFeatured());
            agentPackage.setSettlementPeriod(agentPackageRequest.getSettlementPeriod());
            agentPackage.setPlanPrice(agentPackageRequest.getPlanPrice());
            agentPackage.setActive(agentPackageRequest.getActive());
            agentPackageRepository.save(agentPackage);

            packageOperationPermissionService.updateAll(agentPackage, agentPackageRequest.getOperationPermissionProfiles());
            packageCurrencyLimitService.updateAll(agentPackage, agentPackageRequest.getCurrencyLimitProfiles());

            return agentPackageMapper.fromAgentPackageToAgentPackageDto(agentPackage);
        } catch (Exception ex) {
            throw new ObjectUpdateFailedException(ApiError.REQUEST_FORMAT_ERROR, agentPackageRequest.getName());
        }

    }

    @Override
    public void delete(Long packageId) {
        AgentManagementUtils.isValidId(packageId);
        AgentPackage agentPackage = find(packageId);
        agentPackageRepository.delete(agentPackage);
    }

    @Override
    public void delete(String packageId) {
        try {
            delete(Long.parseLong(packageId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public void updateStatus(Long packageId, Boolean active) {
        AgentPackage agentPackage = find(packageId);
        agentPackage.setActive(active);
        agentPackageRepository.save(agentPackage);
    }

    private void enrichDtoWithCollections(AgentPackageDto agentPackageDto, AgentPackage agentPackage) {
        agentPackageDto.setOperationPermissionProfiles(packageOperationPermissionService.getAllByAgentPackage(agentPackage));
        agentPackageDto.setCurrencyLimitProfiles(packageCurrencyLimitService.getAllByAgentPackage(agentPackage));
    }
}
