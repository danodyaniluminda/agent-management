package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.config.Constants;
import com.digibank.agentmanagement.domain.*;
import com.digibank.agentmanagement.domain.packagemanagement.AgentUser;
import com.digibank.agentmanagement.domain.packagemanagement.UserRole;
import com.digibank.agentmanagement.enums.UserStatus;
import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.exception.ObjectAlreadyExistException;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.mapper.packagemanagement.AgentUserMapper;
import com.digibank.agentmanagement.repository.packagemanagement.AgentUserRepository;
import com.digibank.agentmanagement.service.*;
import com.digibank.agentmanagement.service.packagemanagement.AgentUserService;
import com.digibank.agentmanagement.service.packagemanagement.UserRoleService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.packagemanagement.AgentUserDto;
import com.digibank.agentmanagement.web.dto.ValidateAgentUserDto;
import com.digibank.agentmanagement.utils.ApiError;
import lombok.AllArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AgentUserServiceImpl implements AgentUserService {

    private final AgentUserRepository agentUserRepository;
    private final AgentUserMapper agentUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakService keycloakService;
    private final NotificationService notificationService;

    private final UserRoleService userRoleService;
    private final AgentDetailsService agentDetailsService;

    @Override
    public List<AgentUserDto> getAll() {
        List<AgentUserDto> agentUsers = new ArrayList<>();
        agentUserRepository.findAll().forEach(agentUser -> {
            AgentUserDto agentUserDto = agentUserMapper.fromAgentUserToAgentUserDto(agentUser);
            agentUsers.add(agentUserDto);
        });
        return agentUsers;
    }

    @Override
    public AgentUserDto get(Long agentUserId) {
        AgentManagementUtils.isValidId(agentUserId);
        AgentUser agentUser = agentUserRepository.findById(agentUserId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        return agentUserMapper.fromAgentUserToAgentUserDto(agentUser);
    }

    @Override
    public AgentUser find(Long agentUserId) {
        AgentManagementUtils.isValidId(agentUserId);
        return agentUserRepository.findById(agentUserId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public AgentUser findByUserName(String userName) {
        return agentUserRepository.findByUserName(userName)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public AgentUserDto add(KeycloakAuthenticationToken token, AgentUserDto agentUserRequest) {

        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
        UserRole userRole = userRoleService.find(agentUserRequest.getUserRoleId());

        if(agentUserRepository.findByUserName(agentUserRequest.getUserName()).isPresent()) {
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, agentUserRequest.getUserName());
        }

        if(agentUserRepository.findByEmail(agentUserRequest.getEmail()).isPresent()) {
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, agentUserRequest.getEmail());
        }

        if(agentUserRepository.findByMobileNumber(agentUserRequest.getMobileNumber()).isPresent()) {
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, agentUserRequest.getMobileNumber());
        }

        AgentUser agentUser = agentUserMapper.fromAgentUserDtoToAgentUser(agentUserRequest);
        agentUser.setUserStatus(UserStatus.DISABLE);
        agentUser.setAgentDetails(agentDetails);
        agentUser.setUserRole(userRole);
        agentUserRepository.save(agentUser);

        String keycloakId =
                keycloakService.createAgentUser(
                        agentUser.getEmail(),
                        agentDetails.getPhoneNumberCountryCode(),
                        agentUser.getEmail(),
                        Constants.Roles.ROLE_ADMIN.toString(),
                        agentUser.getAgentUserId()
                );
        agentUser.setUserIamId(keycloakId);
        agentUserRepository.save(agentUser);

        notificationService.generateMFACodeAndSendCore(
                UserType.ADMIN.toString(),locale.en.toString() , agentUser.getEmail(), agentUser.getMobileNumber(), agentUser.getAgentUserId(),
                MFAChannel.EMAIL,
                Constants.EmailTemplate.CUSTOMER_REGISTRATION_PIN,
                Constants.SMSTemplate.CUSTOMER_REGISTRATION_PIN,
                Map.of("name", agentUser.getName() ) , false , true , false );

        return agentUserMapper.fromAgentUserToAgentUserDto(agentUser);
    }

    @Override
    public AgentUserDto update(KeycloakAuthenticationToken token, Long agentUserId, AgentUserDto agentUserRequest) {

        AgentDetails agentDetails = agentDetailsService.findActiveAgentsByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
        UserRole userRole = userRoleService.find(agentUserRequest.getUserRoleId());

        if(agentUserRepository.findByUserNameAndAgentUserIdNot(agentUserRequest.getUserName(), agentUserId).isPresent()) {
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, agentUserRequest.getUserName());
        }

        if(agentUserRepository.findByEmailAndAgentUserIdNot(agentUserRequest.getEmail(), agentUserId).isPresent()) {
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, agentUserRequest.getEmail());
        }

        if(agentUserRepository.findByMobileNumberAndAgentUserIdNot(agentUserRequest.getMobileNumber(), agentUserId).isPresent()) {
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, agentUserRequest.getMobileNumber());
        }

        AgentUser agentUser = find(agentUserId);
        agentUser.setName(agentUserRequest.getName());
        agentUser.setUserName(agentUserRequest.getUserName());
        agentUser.setEmail(agentUserRequest.getEmail());
        agentUser.setMobileNumber(agentUserRequest.getMobileNumber());
//        agentUser.setPassword(passwordEncoder.encode(agentUserRequest.getPassword()));
        agentUser.setUserStatus(agentUserRequest.getUserStatus());
        agentUser.setAgentDetails(agentDetails);
        agentUser.setUserRole(userRole);
        agentUserRepository.save(agentUser);
        return agentUserMapper.fromAgentUserToAgentUserDto(agentUser);
    }

    @Override
    public void delete(Long agentUserId) {
        AgentManagementUtils.isValidId(agentUserId);
        AgentUser agentUser = find(agentUserId);
        agentUserRepository.delete(agentUser);
    }

    @Override
    public void delete(String agentUserId) {
        try {
            delete(Long.parseLong(agentUserId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public AgentUserDto validateAgentUser(ValidateAgentUserDto validateAgentUserDto) {

        AgentUser agentUser = agentUserRepository.findByEmail(validateAgentUserDto.getUserEmail())
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));

        boolean validateMFAToken = notificationService.validateMFACore(agentUser.getMobileNumber(), validateAgentUserDto.getUserEmail(),
                agentUser.getAgentUserId(), validateAgentUserDto.getMfaToken(), UserType.ADMIN.toString());
        if (!validateMFAToken) {
            throw new ObjectNotFoundException(ApiError.INVALID_MFA_TOKEN);
        }
        agentUser.setPassword(passwordEncoder.encode(validateAgentUserDto.getUserPassword()));
        agentUser.setUserStatus(UserStatus.ENABLE);
        agentUserRepository.save(agentUser);
        keycloakService.resetKeycloakUserPasswordCore(
                validateAgentUserDto.getUserEmail(), validateAgentUserDto.getUserPassword());
        keycloakService.enableUserCore(validateAgentUserDto.getUserEmail());
        return agentUserMapper.fromAgentUserToAgentUserDto(agentUser);
    }
}
