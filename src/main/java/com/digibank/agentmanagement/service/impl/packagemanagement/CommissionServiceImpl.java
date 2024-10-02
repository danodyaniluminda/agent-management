package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.packagemanagement.*;
import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.exception.ObjectAlreadyExistException;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.mapper.packagemanagement.CommissionMapper;
import com.digibank.agentmanagement.repository.packagemanagement.CommissionRepository;
import com.digibank.agentmanagement.repository.packagemanagement.PackageAssetOperationPermissionRepository;
import com.digibank.agentmanagement.service.AgentDetailsService;
import com.digibank.agentmanagement.service.packagemanagement.CommissionService;
import com.digibank.agentmanagement.service.packagemanagement.OperationService;
import com.digibank.agentmanagement.service.packagemanagement.PackageOperationPermissionService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.dto.commission.AgentCommissionDTO;
import com.digibank.agentmanagement.web.dto.packagemanagement.CommissionDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CommissionServiceImpl implements CommissionService {

    private final CommissionRepository commissionRepository;
    private final CommissionMapper commissionMapper;

    @Autowired
    private OperationService operationService;

    @Autowired
    private AgentDetailsService agentDetailsService;

    @Autowired
    private PackageAssetOperationPermissionRepository packageAssetOperationPermissionRepository;

    @Override
    public Commission find(Long commissionId) {
        AgentManagementUtils.isValidId(commissionId);
        return commissionRepository.findById(commissionId)
            .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public Commission find(String commissionId) {
        try {
            return find(Long.parseLong(commissionId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public CommissionDto get(Long commissionId) {
        AgentManagementUtils.isValidId(commissionId);
        Commission commission = commissionRepository.findById(commissionId)
            .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        return commissionMapper.fromCommissionToCommissionDto(commission);
    }

    @Override
    public List<CommissionDto> getAll() {
        List<CommissionDto> commissions = new ArrayList<>();
        commissionRepository.findAll().forEach(commission -> {
            CommissionDto commissionDto = commissionMapper.fromCommissionToCommissionDto(commission);
            commissions.add(commissionDto);
        });
        return commissions;
    }

    @Override
    public CommissionDto add(CommissionDto commissionRequest) {
        if (commissionRepository.findByCommissionType(commissionRequest.getCommissionType()).isPresent()) {
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, commissionRequest.getCommissionType().name());
        }

        Commission commission = commissionMapper.fromCommissionDtoToCommission(commissionRequest);
        commission.setCreateDate(Instant.now());
        commissionRepository.save(commission);
        return commissionMapper.fromCommissionToCommissionDto(commission);
    }

    @Override
    public CommissionDto update(Long commissionId, CommissionDto commissionRequest) {
        if (commissionRepository.findByCommissionTypeAndCommissionIdNot(commissionRequest.getCommissionType(), commissionId).isPresent()) {
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, commissionRequest.getCommissionType().name());
        }

        Commission commission = find(commissionId);
        commission.setCommissionType(commissionRequest.getCommissionType());
        commission.setCommissionAmount(commissionRequest.getCommissionAmount());
        commission.setCommissionPercentage(commissionRequest.getCommissionPercentage());
        commission.setActive(commissionRequest.getActive());
        commissionRepository.save(commission);
        return commissionMapper.fromCommissionToCommissionDto(commission);
    }

    @Override
    public void delete(Long commissionId) {
        AgentManagementUtils.isValidId(commissionId);
        Commission commission = find(commissionId);
        commissionRepository.delete(commission);
    }

    @Override
    public void delete(String commissionId) {
        try {
            delete(Long.parseLong(commissionId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public Commission findByCommissionType(CommissionType commissionType) {
        return commissionRepository.findByCommissionType(commissionType)
            .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public AgentCommissionDTO getCommissionByAgent(String agentMobileNumber, String operationName) {
        AgentCommissionDTO agentCommissionDTO = new AgentCommissionDTO();
        DecimalFormat commissionAmountFormat = new DecimalFormat("0.00");
        double commissionAmount = 0.00;

//        Get Agent details by agentId
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(agentMobileNumber);
        if (agentDetails == null) {
            throw new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND);
        }

        //Get operation details by agent
        Operation walletOperation = operationService.findByName(operationName);
        if (walletOperation == null) {
            throw new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND);
        }

        //Get commission details by package and operation
        PackageOperationPermission packageOperationPermission = checkPermissionsForOperationInPackage(walletOperation, agentDetails.getAgentPackage());

        Commission agentCommission = packageOperationPermission.getCommission();
        if (agentCommission == null || !agentCommission.getActive()) {
            throw new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND);
        }

        if (agentCommission.getCommissionType().equals(CommissionType.FIXED)){
            commissionAmount = (double) agentCommission.getCommissionAmount();
        }

        if (agentCommission.getCommissionType().equals(CommissionType.PERCENTAGE)){
            commissionAmount = (agentCommission.getCommissionPercentage() * agentCommission.getCommissionPercentage()) / 100;
        }


        agentCommissionDTO.setCommissionAmount(commissionAmount);
        agentCommissionDTO.setCommissionFixedAmount(agentCommission.getCommissionAmount());
        agentCommissionDTO.setCommissionPercentage(agentCommission.getCommissionPercentage());
        agentCommissionDTO.setCommissionType(agentCommission.getCommissionType());
        agentCommissionDTO.setAgentType(agentDetails.getAgentType());
        agentCommissionDTO.setAgentRegisteredBy(agentDetails.getAgentRegisteredBy());
        agentCommissionDTO.setSuperAgentId(agentDetails.getSuperAgentId());
        agentCommissionDTO.setAgentLinkedTo(agentDetails.getAgentLinkedTo());
        agentCommissionDTO.setAgentId(agentDetails.getAgentId());

        return agentCommissionDTO;
    }

    @Override
    public BigDecimal calculateCommissionByAgent(String agentMobileNumber, String operationName, BigDecimal feeAmount) {
        BigDecimal commissionAmount = BigDecimal.ZERO;


//        Get Commission
        AgentCommissionDTO agentCommission = getCommissionByAgent(agentMobileNumber, operationName);
        if (agentCommission.getCommissionType().equals(CommissionType.FIXED)){
            commissionAmount = BigDecimal.valueOf(agentCommission.getCommissionAmount());
        }

        if (agentCommission.getCommissionType().equals(CommissionType.PERCENTAGE)){
            commissionAmount = feeAmount.multiply(BigDecimal.valueOf(agentCommission.getCommissionPercentage())).divide(BigDecimal.valueOf(100), RoundingMode.UP);
        }


        return commissionAmount;
    }

    private PackageOperationPermission checkPermissionsForOperationInPackage(Operation operation, AgentPackage agentPackage) {
        PackageOperationPermission packageOperationPermission =  packageAssetOperationPermissionRepository.findByOperationAndAgentPackage(operation, agentPackage).orElseThrow(()-> new ObjectNotFoundException(ApiError.PACKAGE_OPERATION_NOT_AVAILABLE));
        if(!packageOperationPermission.getActive()){
            throw new ObjectNotFoundException(ApiError.PACKAGE_OPERATION_NOT_AVAILABLE);
        }
        return packageOperationPermission;
    }
}

