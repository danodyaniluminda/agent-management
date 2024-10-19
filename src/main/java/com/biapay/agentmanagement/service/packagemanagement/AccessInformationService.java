package com.biapay.agentmanagement.service.packagemanagement;

import com.biapay.agentmanagement.web.dto.packagemanagement.AccessInformationDto;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

import java.util.List;

public interface AccessInformationService {

    List<AccessInformationDto> getAll(String userName);

    AccessInformationDto get(Long accessInfoId);

    AccessInformationDto add(KeycloakAuthenticationToken token, AccessInformationDto accessInfoRequest);
}
