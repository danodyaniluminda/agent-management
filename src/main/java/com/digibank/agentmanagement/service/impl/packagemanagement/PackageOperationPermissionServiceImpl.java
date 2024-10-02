package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.*;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.repository.packagemanagement.PackageAssetOperationPermissionRepository;
import com.digibank.agentmanagement.service.packagemanagement.AssetService;
import com.digibank.agentmanagement.service.packagemanagement.CommissionService;
import com.digibank.agentmanagement.service.packagemanagement.OperationService;
import com.digibank.agentmanagement.service.packagemanagement.PackageOperationPermissionService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.dto.packagemanagement.requests.AssetOperationPermissionDto;
import com.digibank.agentmanagement.web.dto.packagemanagement.OperationPermissionDto;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PackageOperationPermissionServiceImpl implements PackageOperationPermissionService {

    @Autowired
    private PackageAssetOperationPermissionRepository packageAssetOperationPermissionRepository;

    private final AssetService assetService;
    private final OperationService operationService;
    private final CommissionService commissionService;

    @Override
    public void addAll(AgentPackage agentPackage, List<AssetOperationPermissionDto> assetOperationPermissions) {
        if (CollectionUtils.isNotEmpty(assetOperationPermissions)) {
            assetOperationPermissions.forEach(assetOperationPermissionDto -> {
                Asset asset = assetService.findByName(assetOperationPermissionDto.getAssetName());
                if (CollectionUtils.isNotEmpty(assetOperationPermissionDto.getOperationPermissions())) {
                    assetOperationPermissionDto.getOperationPermissions().forEach(operationPermissionDto -> {
                        Operation operation = operationService.findByNameAndAsset(operationPermissionDto.getOperationName() , asset);
                        Commission commission = commissionService.findByCommissionType(operationPermissionDto.getCommissionType());
                        PackageOperationPermission packageOperationPermission = new PackageOperationPermission();
                        packageOperationPermission.setActive(operationPermissionDto.getActive());
                        packageOperationPermission.setCreateDate(Instant.now());
                        packageOperationPermission.setOperation(operation);
                        packageOperationPermission.setCommission(commission);
                        packageOperationPermission.setAgentPackage(agentPackage);
                        packageAssetOperationPermissionRepository.save(packageOperationPermission);
                    });
                }
            });
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void updateAll(AgentPackage agentPackage, List<AssetOperationPermissionDto> assetOperationPermissions) {
        packageAssetOperationPermissionRepository.deleteByAgentPackage(agentPackage);
        if (CollectionUtils.isNotEmpty(assetOperationPermissions)) {
            assetOperationPermissions.forEach(assetOperationPermissionDto -> {
                Asset asset = assetService.findByName(assetOperationPermissionDto.getAssetName());
                if (CollectionUtils.isNotEmpty(assetOperationPermissionDto.getOperationPermissions())) {
                    assetOperationPermissionDto.getOperationPermissions().forEach(operationPermissionDto -> {
                        Operation operation = operationService.findByNameAndAsset(operationPermissionDto.getOperationName() , asset);
                        Commission commission = commissionService.findByCommissionType(operationPermissionDto.getCommissionType());
                        PackageOperationPermission packageOperationPermission = new PackageOperationPermission();
                        packageOperationPermission.setActive(operationPermissionDto.getActive());
                        packageOperationPermission.setCreateDate(Instant.now());
                        packageOperationPermission.setOperation(operation);
                        packageOperationPermission.setCommission(commission);
                        packageOperationPermission.setAgentPackage(agentPackage);
                        packageAssetOperationPermissionRepository.save(packageOperationPermission);
                    });
                }
            });
        }
    }

    @Override
    public PackageOperationPermission checkPermissionsForOperationInPackage(Operation operation, AgentPackage agentPackage) {
        PackageOperationPermission packageOperationPermission =  packageAssetOperationPermissionRepository.findByOperationAndAgentPackage(operation, agentPackage).orElseThrow(()-> new ObjectNotFoundException(ApiError.PACKAGE_OPERATION_NOT_AVAILABLE));
        if(!packageOperationPermission.getActive()){
            throw new ObjectNotFoundException(ApiError.PACKAGE_OPERATION_NOT_AVAILABLE);
        }
        return packageOperationPermission;
    }

    @Override
    public List<AssetOperationPermissionDto> getAllByAgentPackage(AgentPackage agentPackage) {
        List<AssetOperationPermissionDto> assetOperationPermissions = new ArrayList<>();
        List<PackageOperationPermission> packageOperationPermissions =
                packageAssetOperationPermissionRepository.findAllByAgentPackage(agentPackage);
        List<Asset> assets = packageOperationPermissions.stream().map(packageOperationPermission ->
                        packageOperationPermission.getOperation().getAsset()).distinct().collect(Collectors.toList());
        assets.forEach(asset -> {
            List<PackageOperationPermission> packageAssetOperationPermissionsBy = packageOperationPermissions.stream()
                    .filter(packageOperationPermission -> Objects.equals(
                            packageOperationPermission.getOperation().getAsset().getAssetId(), asset.getAssetId())).collect(Collectors.toList());
            List<OperationPermissionDto> operationPermissions = new ArrayList<>();
            packageAssetOperationPermissionsBy.forEach(packageOperationPermission -> {
                OperationPermissionDto operationPermission = OperationPermissionDto.builder()
                        .operationName(packageOperationPermission.getOperation().getName())
                        .commissionType(packageOperationPermission.getCommission().getCommissionType())
                        .active(packageOperationPermission.getActive())
                        .build();
                operationPermissions.add(operationPermission);
            });
            AssetOperationPermissionDto assetOperationPermissionDto = AssetOperationPermissionDto.builder()
                    .assetName(asset.getName()).operationPermissions(operationPermissions).build();
            assetOperationPermissions.add(assetOperationPermissionDto);
        });
        return assetOperationPermissions;
    }

    @Override
    public List<PackageOperationPermission> findAllByAgentPackage(AgentPackage agentPackage) {
        return packageAssetOperationPermissionRepository.findAllByAgentPackage(agentPackage);
    }
}
