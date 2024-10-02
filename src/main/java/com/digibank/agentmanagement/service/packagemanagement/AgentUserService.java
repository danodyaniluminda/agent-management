package com.digibank.agentmanagement.service.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.AgentUser;
import com.digibank.agentmanagement.web.dto.packagemanagement.AgentUserDto;
import com.digibank.agentmanagement.web.dto.ValidateAgentUserDto;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

import java.util.List;

public interface AgentUserService {

    List<AgentUserDto> getAll();

    AgentUserDto get(Long agentUserId);

    AgentUser find(Long agentUserId);

    AgentUser findByUserName(String userName);

    AgentUserDto add(KeycloakAuthenticationToken token, AgentUserDto agentUserRequest);

    AgentUserDto update(KeycloakAuthenticationToken token, Long agentUserId, AgentUserDto agentUserRequest);

    void delete(Long agentUserId);

    void delete(String agentUserId);

    AgentUserDto validateAgentUser(ValidateAgentUserDto validateAgentUserDto);
}
