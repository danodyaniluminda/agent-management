package com.biapay.agentmanagement.service.impl.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.AccessInformation;
import com.biapay.agentmanagement.domain.packagemanagement.AgentUser;
import com.biapay.agentmanagement.exception.ObjectNotFoundException;
import com.biapay.agentmanagement.mapper.packagemanagement.AccessInformationMapper;
import com.biapay.agentmanagement.repository.packagemanagement.AccessInformationRepository;
import com.biapay.agentmanagement.service.packagemanagement.AccessInformationService;
import com.biapay.agentmanagement.service.packagemanagement.AgentUserService;
import com.biapay.agentmanagement.utils.AgentManagementUtils;
import com.biapay.agentmanagement.web.dto.packagemanagement.AccessInformationDto;
import com.biapay.agentmanagement.utils.ApiError;
import lombok.AllArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccessInformationServiceImpl implements AccessInformationService {

    private final AccessInformationRepository accessInformationRepository;
    private final AccessInformationMapper accessInformationMapper;

    private final AgentUserService agentUserService;

    @Override
    public List<AccessInformationDto> getAll(String userName) {
        if (userName != null) {
            return accessInformationRepository.findAllByUserName(userName).stream()
                    .map(accessInformationMapper::fromAccessInformationToAccessInformationDto)
                    .collect(Collectors.toList());
        } else {
            return accessInformationRepository.findAll().stream()
                    .map(accessInformationMapper::fromAccessInformationToAccessInformationDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public AccessInformationDto get(Long accessInfoId) {
        AgentManagementUtils.isValidId(accessInfoId);
        return accessInformationMapper.fromAccessInformationToAccessInformationDto(
                accessInformationRepository.findById(accessInfoId)
                        .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND))
        );
    }

    @Override
    public AccessInformationDto add(KeycloakAuthenticationToken token, AccessInformationDto accessInfoRequest) {
        AgentUser agentUser = agentUserService.findByUserName(AgentManagementUtils.getLoggedInUserUsername(token));
        AccessInformation accessInformation = accessInformationMapper.fromAccessInformationDtoToAccessInformation(accessInfoRequest);
        accessInformation.setAccessDate(Instant.now());
        accessInformation.setUserName(agentUser.getUserName());
        return accessInformationMapper.fromAccessInformationToAccessInformationDto(accessInformationRepository.save(accessInformation));
    }
}
