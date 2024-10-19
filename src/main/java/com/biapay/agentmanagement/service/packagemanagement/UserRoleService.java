package com.biapay.agentmanagement.service.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.UserRole;
import com.biapay.agentmanagement.web.dto.packagemanagement.UserRoleDto;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

import java.util.List;

public interface UserRoleService {

    UserRole find(Long userRoleId);

    UserRole find(String userRoleId);

    UserRole findByName(String userName);

    UserRoleDto get(Long userRoleId);

    List<UserRoleDto> getAll();

    UserRoleDto add(KeycloakAuthenticationToken token, UserRoleDto userRoleRequest);

    UserRoleDto update(KeycloakAuthenticationToken token, Long userRoleId, UserRoleDto userRoleRequest);

    void delete(Long userRoleId);

    void delete(String userRoleId);
}
