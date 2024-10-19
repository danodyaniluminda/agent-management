package com.biapay.agentmanagement.service;

import static com.biapay.agentmanagement.utils.ApiError.AGENT_ALREADY_EXISTS_WITH_THIS_EMAIL;
import static com.biapay.agentmanagement.utils.ApiError.AGENT_ALREADY_EXISTS_WITH_THIS_PHONE_NUMBER;
import static com.biapay.agentmanagement.utils.ApiError.INVALID_SUPER_AGENT_DETAILS;
import static com.biapay.agentmanagement.utils.ApiError.INVALID_SUPER_AGENT_STATUS;

import com.biapay.agentmanagement.config.Constants;
import com.biapay.agentmanagement.domain.AgentDetails;
import com.biapay.agentmanagement.domain.AgentStatus;
import com.biapay.agentmanagement.domain.AgentType;
import com.biapay.agentmanagement.domain.packagemanagement.AgentPackage;
import com.biapay.agentmanagement.exception.BIAPayRuntimeException;
import com.biapay.agentmanagement.exception.BadRequestException;
import com.biapay.agentmanagement.mapper.AgentServiceMapper;
import com.biapay.agentmanagement.repository.AgentDetailsRepository;
import com.biapay.agentmanagement.repository.AgentLinkingRepository;
import com.biapay.agentmanagement.repository.AgentPackageRepository;
import com.biapay.agentmanagement.repository.DocumentRepository;
import com.biapay.agentmanagement.repository.packagemanagement.AgentLimitProfileRepository;
import com.biapay.agentmanagement.repository.packagemanagement.CurrencyRepository;
import com.biapay.agentmanagement.repository.packagemanagement.PackageCurrencyLimitRepository;
import com.biapay.agentmanagement.service.packagemanagement.BackOfficeService;
import com.biapay.agentmanagement.utils.KeycloakUtil;
import com.biapay.agentmanagement.web.dto.RegistrationRes;
import com.biapay.agentmanagement.web.dto.agentregistration.request.RegistrationReq;
import com.biapay.core.constant.BIAConstants.Role;
import com.biapay.core.constant.enums.KycApprovalStatus;
import com.biapay.core.model.User;
import com.biapay.core.model.UserStatus;
import com.biapay.core.model.UserType;
import com.biapay.core.repository.RoleRepository;
import com.biapay.core.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zalando.problem.Status;

@Service
@Slf4j
public class AgentRegistrationService {

    private final AgentServiceMapper agentServiceMapper;
    private final AgentDetailsRepository agentDetailsRepository;
    private final String uploadFolder;
    private final DocumentRepository documentRepository;
    private final AgentPackageRepository agentPackageRepository;
    private final AgentLimitProfileRepository agentLimitProfileRepository;
    private final PackageCurrencyLimitRepository packageCurrencyLimitRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;
    private final AgentLinkingRepository agentLinkingRepository;

    private final KeycloakService keycloakService;
    private final NotificationService notificationService;
    private final WalletService walletService;
    private final BackOfficeService backOfficeService;
    private final RoleRepository roleRepository;
    private final KeycloakUtil keycloakUtil;

    public AgentRegistrationService(
        AgentServiceMapper agentServiceMapper,
        AgentDetailsRepository agentDetailsRepository,
        @Value("${fileStore.basePath}") String uploadFolder,
        DocumentRepository documentRepository,

        KeycloakService keycloakService,
        AgentLinkingRepository agentLinkingRepository,
        NotificationService notificationService,

        WalletService walletService,
        AgentPackageRepository agentPackageRepository,
        AgentLimitProfileRepository agentLimitProfileRepository,
        PackageCurrencyLimitRepository packageCurrencyLimitRepository,
        CurrencyRepository currencyRepository,
        BackOfficeService backOfficeService, UserRepository userRepository,
        RoleRepository roleRepository, KeycloakUtil keycloakUtil) {
        this.agentServiceMapper = agentServiceMapper;
        this.agentDetailsRepository = agentDetailsRepository;
        this.uploadFolder = uploadFolder;
        this.documentRepository = documentRepository;
        this.agentLimitProfileRepository = agentLimitProfileRepository;

        this.keycloakService = keycloakService;
        this.agentLinkingRepository = agentLinkingRepository;
        this.notificationService = notificationService;

        this.walletService = walletService;
        this.agentPackageRepository = agentPackageRepository;
        this.packageCurrencyLimitRepository = packageCurrencyLimitRepository;
        this.currencyRepository = currencyRepository;
        this.backOfficeService = backOfficeService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.keycloakUtil = keycloakUtil;
    }

    @Transactional
    public RegistrationRes registerAgent(RegistrationReq registrationReq) {
        log.info("Agent Registration");
//        if (registrationReq.getAgentType() == AgentType.SUPER_AGENT || registrationReq.getAgentType() == AgentType.AGENT_BANKER){
//            if (token != null){
//                LoggedInUser loggedInUser = AuthUtil.getLoggedInUser(token);
//
//                if (loggedInUser == null || loggedInUser.getUserType() != UserType.ADMIN) {
//                    throw new BadRequestException("Only admin can create super agent!");
//                }
//            }
//            else{
//                throw new BadRequestException("Only admin can create super agent!");
//            }
//        }
//
//        if (registrationReq.getAgentType() == AgentType.AGENT_MEMBER){
//            if (token != null){
//                LoggedInUser loggedInUser = AuthUtil.getLoggedInUser(token);
//
//                if (loggedInUser == null || loggedInUser.getUserType() != UserType.AGENT) {
//                    throw new BadRequestException("Only Super-Agent or Agent can register an Agent-member!");
//                }
//            }
//            else{
//                throw new BadRequestException("Only Super-Agent or Agent can register an Agent-member!");
//            }
//        }

        validateRequest(registrationReq);

        Optional<AgentPackage> defaultPackageOptional = Optional.empty();

//        if (registrationReq.getAgentType() != AgentType.AGENT_MEMBER) {
//            defaultPackageOptional = agentPackageRepository.findByAgentTypeAndIsDefault(registrationReq.getAgentType(), true);
//            if (defaultPackageOptional.isEmpty()) {
//                throw new BadRequestException(DEFAULT_PACKAGE_ERROR);
//            }
//        }
//        else {
//          defaultPackageOptional = agentPackageRepository.findByPackageId(registrationReq.getPackageId());
//          if (defaultPackageOptional.isEmpty() || defaultPackageOptional.get().getAgentType() != AgentType.AGENT_MEMBER) {
//              throw new BadRequestException("PACKAGE_ERROR");
//          }
//
//        }

        String keycloakId = null;
        RegistrationRes registrationRes = null;

        try {

            AgentDetails agentDetails = agentServiceMapper.fromAgentRegistrationReq(registrationReq);

            // TODO
            agentDetails.setCreatedBy(Constants.SYSTEM);
            agentDetails.setLastModifiedBy(Constants.SYSTEM);
            agentDetails.setStatus(AgentStatus.ACTIVE);

            if (agentDetails.getAgentType() == AgentType.AGENT_MEMBER) {
                agentDetails.setSuperAgentId(
                    agentDetailsRepository.findByPhoneNo(registrationReq.getPhoneNo()).get().getIamId());

                agentDetails.setStatus(AgentStatus.ACTIVE);
            }

            // Saving images and Documents
            agentDetailsRepository.save(agentDetails);
//            saveAgentPhoto(registrationReq, agentDetails);
//            saveDocumentIdInfo(registrationReq, agentDetails);
//            saveAddressProofDocuments(registrationReq, agentDetails);
            // Disable Terms and condition Data
//            TermsConditions termsConditions = setAgentTermsAndConditions(registrationReq);
//            agentDetails.setTermsConditions(termsConditions);
            agentDetails.setRegistrationDateTime(LocalDateTime.now());
            // Saving Agent into agent Linking table
            agentDetailsRepository.save(agentDetails);

            String referralCode = registrationReq.getEmail().split("@")[0] + new Random().nextInt(9)
                + userRepository.count();
            User user = new User();
            user.setEmail(registrationReq.getEmail());
            // enable merchant after his registration
            user.setUserStatus(UserStatus.ACTIVE);
            user.setUsername(registrationReq.getAgentName());

            UserType userType = UserType.AGENT;
            String userRole = Role.ROLE_AGENT;

            if (registrationReq.getAgentType() == AgentType.AGENT_MEMBER) {
                userType = UserType.AGENT_MEMBER;
            }

            user.setUserType(userType);
            user.setRoles(
                Stream.of(roleRepository.findByName(Role.ROLE_MERCHANT)).collect(Collectors.toSet()));
            user.setEmailVerificationToken(UUID.randomUUID().toString());
            user.setEmailVerified(false);
            user.setName(registrationReq.getAgentName());
            user.setFirstName(registrationReq.getFirstName());
            user.setLastName(registrationReq.getLastName());
            user.setMobileNumber(registrationReq.getPhoneNo());
            user.setTwoFAEnabled(registrationReq.getTwoFactorStatus());
            user.setReferralCode(referralCode);
            user.setKycStatus(KycApprovalStatus.PENDING_KYC_SUBMISSION);
            user = userRepository.save(user);

            User finalUser = user;

            agentDetails.setRootUser(user);
            agentDetails = agentDetailsRepository.save(agentDetails);

            String username =
                (registrationReq.getEmail() == null || registrationReq.getEmail().isEmpty())
                    ? registrationReq.getPhoneNo()
                    : registrationReq.getEmail();

            keycloakUtil.createUser(
                username,
                registrationReq.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                registrationReq.getEmail(),
                user.getUserType(),
                Collections.singletonList(Role.ROLE_MERCHANT));

            // insertion into Agent Limit Profile Table

//            updateAgentLimitProfile(agentDetails, defaultPackageOptional.get());

            log.info("inside I am ID {}", keycloakId);
            agentDetails.setIamId(keycloakId);

            agentDetailsRepository.save(agentDetails);

            if (registrationReq.getAgentType() == AgentType.AGENT_MEMBER) {
                keycloakService.resetKeycloakUserPassword(
                    registrationReq.getPhoneNo(), registrationReq.getPassword());
                keycloakService.enableUser(agentDetails);
            }

            registrationRes = RegistrationRes.builder().iamId(agentDetails.getIamId()).build();

//            if (registrationReq.getAgentType() != AgentType.AGENT_MEMBER) {
//                this.notifyBankCustomerRegistration(agentDetails);
//            } else {
//                //TODO send password by email to agent member flow is pending
//            }
//            walletService.createAgentWallet(agentDetails.getAgentId().toString(), UserType.AGENT.toString(), agentDetails.getCountryCode(), agentDetails.getCurrency());
//            backOfficeService.AgentRegistrationBackoffice(agentDetails);

            notificationService.agentRegistrationEmailConfirmation(agentDetails);

        } catch (BIAPayRuntimeException e) {
            if (keycloakId != null) {
                keycloakService.deleteUser(Constants.KEYCLOAK_AGENT_PREFIX + registrationReq.getPhoneNo());
            }

            log.error("Error while registering Agent Message : {}", e.getMessage());
            throw e;
        }

        return registrationRes;
    }


    private void validateRequest(RegistrationReq registrationReq) {
        agentDetailsRepository
            .findByPhoneNo(registrationReq.getPhoneNo())
            .ifPresent(
                agent -> {

                    throw new BadRequestException(AGENT_ALREADY_EXISTS_WITH_THIS_PHONE_NUMBER,
                        Status.BAD_REQUEST, agent.getStatus().toString());
                });
        if (registrationReq.getEmail() != null && !registrationReq.getEmail().isEmpty()) {
            agentDetailsRepository
                .findByAgentEmailAddress(registrationReq.getEmail())
                .ifPresent(
                    agent -> {
                        throw new BadRequestException(AGENT_ALREADY_EXISTS_WITH_THIS_EMAIL,
                            agent.getStatus().toString());
                    });
            if (keycloakService.isUserExistByLogin(
                Constants.KEYCLOAK_AGENT_PREFIX + registrationReq.getPhoneNo())) {
                throw new BadRequestException(AGENT_ALREADY_EXISTS_WITH_THIS_PHONE_NUMBER);
            }

        }

        if (registrationReq.getAgentType() == AgentType.AGENT_MEMBER) {
            Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByPhoneNo(
                registrationReq.getSuperAgentPhone());
            if (!optionalAgentDetails.isPresent()) {
                throw new BadRequestException(INVALID_SUPER_AGENT_DETAILS);
            } else if (optionalAgentDetails.get().getStatus() != AgentStatus.ACTIVE) {
                throw new BadRequestException(INVALID_SUPER_AGENT_STATUS);
            }

            if (registrationReq.getPassword() == null || registrationReq.getPassword().isEmpty()) {
                throw new BadRequestException("PASSWORD FIELD IS REQUIRED FOR AGENT MEMBER");
            }


        }
    }
}
