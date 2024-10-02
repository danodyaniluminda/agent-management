package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.packagemanagement.UserRole;
import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.exception.ObjectAlreadyExistException;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.mapper.packagemanagement.UserRoleMapper;
import com.digibank.agentmanagement.repository.packagemanagement.UserRoleRepository;
import com.digibank.agentmanagement.service.AgentDetailsService;
import com.digibank.agentmanagement.service.packagemanagement.UserRoleService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.packagemanagement.UserRoleDto;
import com.digibank.agentmanagement.utils.ApiError;
import lombok.AllArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRoleMapper userRoleMapper;

    private final AgentDetailsService agentDetailsService;

    @Override
    public UserRole find(Long userRoleId) {
        AgentManagementUtils.isValidId(userRoleId);
        return userRoleRepository.findById(userRoleId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public UserRole find(String userRoleId) {
        try {
            return find(Long.parseLong(userRoleId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public UserRole findByName(String name) {
        return userRoleRepository.findByName(name)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public UserRoleDto get(Long userRoleId) {
        AgentManagementUtils.isValidId(userRoleId);
        UserRole userRole = userRoleRepository.findById(userRoleId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        return userRoleMapper.fromUserRoleToUserRoleDto(userRole);
    }

    @Override
    public List<UserRoleDto> getAll() {
        List<UserRoleDto> userRoleDtos = new ArrayList<>();
        userRoleRepository.findAll().forEach(userRole -> {
            UserRoleDto userRoleDto = userRoleMapper.fromUserRoleToUserRoleDto(userRole);
            userRoleDtos.add(userRoleDto);
        });
        return userRoleDtos;
    }

    @Override
    public UserRoleDto add(KeycloakAuthenticationToken token, UserRoleDto userRoleRequest) {
        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));

        if(userRoleRepository.findByName(userRoleRequest.getName()).isPresent()){
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, userRoleRequest.getName());
        }

        UserRole userRole = userRoleMapper.fromUserRoleDtoToUserRole(userRoleRequest);
        userRole.setAgentDetails(agentDetails);
        userRoleRepository.save(userRole);
        return userRoleMapper.fromUserRoleToUserRoleDto(userRole);
    }

    @Override
    public UserRoleDto update(KeycloakAuthenticationToken token, Long userRoleId, UserRoleDto userRoleRequest) {
        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));

        if(userRoleRepository.findByNameAndUserRoleIdNot(userRoleRequest.getName(), userRoleId).isPresent()){
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, userRoleRequest.getName());
        }

        UserRole userRole = find(userRoleId);
        userRole.setName(userRoleRequest.getName());
        userRole.setDisplayName(userRoleRequest.getDisplayName());
        userRole.setDescription(userRoleRequest.getDescription());
        userRole.setDefaultRole(userRoleRequest.isDefaultRole());
        userRole.setAgentDetails(agentDetails);
        userRoleRepository.save(userRole);
        return userRoleMapper.fromUserRoleToUserRoleDto(userRole);
    }

    @Override
    public void delete(Long userRoleId) {
        AgentManagementUtils.isValidId(userRoleId);
        UserRole userRole = find(userRoleId);
        userRoleRepository.delete(userRole);
    }

    @Override
    public void delete(String userRoleId) {
        try {
            delete(Long.parseLong(userRoleId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }
}
