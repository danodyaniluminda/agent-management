package com.digibank.agentmanagement.service;

import com.digibank.agentmanagement.config.Constants;
import com.digibank.agentmanagement.domain.*;
import com.digibank.agentmanagement.domain.packagemanagement.AgentLimitProfile;
import com.digibank.agentmanagement.domain.packagemanagement.AgentPackage;
import com.digibank.agentmanagement.domain.packagemanagement.PackageCurrencyLimit;
import com.digibank.agentmanagement.exception.BadRequestException;
import com.digibank.agentmanagement.exception.DigibankRuntimeException;
import com.digibank.agentmanagement.mapper.AgentServiceMapper;
import com.digibank.agentmanagement.repository.AgentDetailsRepository;
import com.digibank.agentmanagement.repository.AgentLinkingRepository;
import com.digibank.agentmanagement.repository.AgentPackageRepository;
import com.digibank.agentmanagement.repository.DocumentRepository;
import com.digibank.agentmanagement.repository.packagemanagement.AgentLimitProfileRepository;
import com.digibank.agentmanagement.repository.packagemanagement.CurrencyRepository;
import com.digibank.agentmanagement.repository.packagemanagement.PackageCurrencyLimitRepository;
import com.digibank.agentmanagement.service.packagemanagement.BackOfficeService;
import com.digibank.agentmanagement.web.dto.RegistrationRes;
import com.digibank.agentmanagement.web.dto.agentregistration.request.RegistrationReq;
import com.digibank.agentmanagement.web.dto.agentregistration.request.RegistrationSetPasswordReq;
import com.digibank.agentmanagement.web.dto.agentregistration.request.ResendAgentPin;
import com.digibank.agentmanagement.web.dto.agentregistration.request.ValidatePin;
import com.digibank.agentmanagement.web.dto.keycloakutil.LoggedInUser;
import com.digibank.agentmanagement.web.util.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Status;

import javax.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.digibank.agentmanagement.utils.ApiError.*;

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


    private final AgentLinkingRepository agentLinkingRepository;

    private final KeycloakService keycloakService;
    private final NotificationService notificationService;
    private final WalletService walletService;
    private final BackOfficeService backOfficeService;

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
            BackOfficeService backOfficeService) {
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
    }

    private void saveAddressProofDocuments(RegistrationReq registrationReq, AgentDetails agentDetails) {

        if (registrationReq.getProofOfAddress() == null)
            return;

        try {
            for (MultipartFile multipartFile : registrationReq.getProofOfAddress()) {
                String fileName = UUID.randomUUID().toString();
                byte[] bytes = multipartFile.getBytes();
                Path uploadDir = Paths.get(uploadFolder);
                Path path = Paths.get(uploadFolder, fileName);

                if (!Files.exists(uploadDir)) {
                    Files.createDirectory(uploadDir);
                }
                Files.write(path, bytes);

                DocumentIdInfo document =
                        DocumentIdInfo.builder()
                                .documentType(IDDocumentType.ADDRESS_PROOF)
                                .documentFileNameOrignal(multipartFile.getOriginalFilename())
                                .documentFileName(fileName)
                                .linkedModule(LinkedModule.AGENT)
                                .agentDetails(agentDetails)
                                .build();
                agentDetails.addIdDocument(document);
            }
        } catch (Exception e) {
            throw new DigibankRuntimeException(e);
        }
    }

    private void saveDocumentIdInfo(RegistrationReq registrationReq, AgentDetails agentDetails) {


        try {
            for (MultipartFile multipartFile : registrationReq.getIdDocumentImages()) {
                String fileName = UUID.randomUUID().toString();
                byte[] bytes = multipartFile.getBytes();
                Path uploadDir = Paths.get(uploadFolder);
                Path path = Paths.get(uploadFolder, fileName);

                if (!Files.exists(uploadDir)) {
                    Files.createDirectory(uploadDir);
                }
                Files.write(path, bytes);

                DocumentIdInfo document =
                        DocumentIdInfo.builder()
                                .documentType(registrationReq.getIdDocumentType())
                                .documentFileNameOrignal(multipartFile.getOriginalFilename())
                                .documentFileName(fileName)
                                .documentName(registrationReq.getIdDocumentName())
                                .documentIdNumber(registrationReq.getIdDocumentIdNumber())
                                .documentExpiryDate(registrationReq.getIdExpiryDate())
                                .linkedModule(LinkedModule.AGENT)
                                .agentDetails(agentDetails)
                                .build();
                //documentRepository.save(document);
                agentDetails.addIdDocument(document);
            }
        } catch (Exception e) {
            throw new DigibankRuntimeException(e);
        }
    }

    private void saveAgentPhoto(RegistrationReq registrationReq, AgentDetails agentDetails) {
        try {
            if (registrationReq.getAgentPhoto() != null) {
                String fileName = UUID.randomUUID().toString();
                byte[] bytes = registrationReq.getAgentPhoto().getBytes();
                Path uploadDir = Paths.get(uploadFolder);
                Path path = Paths.get(uploadFolder, fileName);

                if (!Files.exists(uploadDir)) {
                    Files.createDirectory(uploadDir);
                }

                Files.write(path, bytes);

                DocumentIdInfo document =
                        DocumentIdInfo.builder()
                                .documentType(IDDocumentType.SELFIE_DOCUMENT)
                                .documentFileNameOrignal(registrationReq.getAgentPhoto().getOriginalFilename())
                                .documentFileName(fileName)
                                .linkedModule(LinkedModule.AGENT)
                                .agentDetails(agentDetails)
                                .build();
                //documentRepository.save(document);
                agentDetails.addSelfieDocument(document);
            }
        } catch (Exception e) {
            throw new DigibankRuntimeException(e);
        }
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

        if (registrationReq.getAgentType() != AgentType.AGENT_MEMBER) {
            defaultPackageOptional = agentPackageRepository.findByAgentTypeAndIsDefault(registrationReq.getAgentType(), true);
            if (defaultPackageOptional.isEmpty()) {
                throw new BadRequestException(DEFAULT_PACKAGE_ERROR);
            }
        }
        else {
          defaultPackageOptional = agentPackageRepository.findByPackageId(registrationReq.getPackageId());
          if (defaultPackageOptional.isEmpty() || defaultPackageOptional.get().getAgentType() != AgentType.AGENT_MEMBER) {
              throw new BadRequestException("PACKAGE_ERROR");
          }

        }


        String keycloakId = null;
        RegistrationRes registrationRes = null;

        try {

            AgentDetails agentDetails = agentServiceMapper.fromAgentRegistrationReq(registrationReq);


            // TODO
            agentDetails.setCreatedBy(Constants.SYSTEM);
            agentDetails.setLastModifiedBy(Constants.SYSTEM);
            agentDetails.setStatus(AgentStatus.PENDING_PIN_VERIFICATION);
            agentDetails.setBusinessType(registrationReq.getBusincessType());
            if (registrationReq.getCity() != null)
                agentDetails.setCity(registrationReq.getCity());
            if (registrationReq.getAddress() != null)
                agentDetails.setAddress(registrationReq.getAddress());
            if (registrationReq.getWebsiteLink() != null)
                agentDetails.setWebsiteLink(registrationReq.getWebsiteLink());
            // assigning default agent package
            agentDetails.setAgentPackage(defaultPackageOptional.get());


            if (agentDetails.getAgentType() == AgentType.AGENT_MEMBER) {
                agentDetails.setSuperAgentId(agentDetailsRepository.findByPhoneNo(registrationReq.getSuperAgentPhone()).get().getIamId());

                agentDetails.setStatus(registrationReq.getStatus());
            }

            // Saving images and Documents
            agentDetailsRepository.save(agentDetails);
            saveAgentPhoto(registrationReq, agentDetails);
            saveDocumentIdInfo(registrationReq, agentDetails);
            saveAddressProofDocuments(registrationReq, agentDetails);
            // Disable Terms and condition Data
//            TermsConditions termsConditions = setAgentTermsAndConditions(registrationReq);
//            agentDetails.setTermsConditions(termsConditions);
            agentDetails.setRegistrationDateTime(LocalDateTime.now());
            // Saving Agent into agent Linking table
            agentDetailsRepository.save(agentDetails);
            keycloakId =
                    keycloakService.createKeycloakUser(
                            Constants.KEYCLOAK_AGENT_PREFIX + registrationReq.getPhoneNo(),
                            registrationReq.getPhoneNumberCountryCode(),
                            registrationReq.getAgentEmailAddress(),
                            Constants.Roles.ROLE_AGENT.toString(),
                            agentDetails.getAgentId()
                    );


            // insertion into Agent Limit Profile Table

            updateAgentLimitProfile(agentDetails, defaultPackageOptional.get());

            log.info("inside I am ID " + keycloakId);
            agentDetails.setIamId(keycloakId);

            agentDetailsRepository.save(agentDetails);


            if (registrationReq.getAgentType() == AgentType.AGENT_MEMBER) {
                keycloakService.resetKeycloakUserPassword(
                        registrationReq.getPhoneNo(), registrationReq.getPassword());
                keycloakService.enableUser(agentDetails);
            }


            registrationRes = RegistrationRes.builder().iamId(agentDetails.getIamId()).build();

            if (registrationReq.getAgentType() != AgentType.AGENT_MEMBER) {
                this.notifyBankCustomerRegistration(agentDetails);
            } else {
                //TODO send password by email to agent member flow is pending
            }
            walletService.createAgentWallet(agentDetails.getAgentId().toString(), UserType.AGENT.toString(), agentDetails.getCountryCode(), agentDetails.getCurrency());
            backOfficeService.AgentRegistrationBackoffice(agentDetails);

        } catch (DigibankRuntimeException e) {
            if (keycloakId != null) {
                keycloakService.deleteUser(Constants.KEYCLOAK_AGENT_PREFIX + registrationReq.getPhoneNo());
            }

            log.error("Error while registering Agent Message : {}", e.getMessage());
            throw e;
        }

        return registrationRes;
    }

    private void updateAgentLimitProfile(AgentDetails agentDetails, AgentPackage agentPackage) {


        List<PackageCurrencyLimit> packageCurrencyLimitList = packageCurrencyLimitRepository.findAllByAgentPackage(agentPackage);
        if (packageCurrencyLimitList.isEmpty())
            throw new BadRequestException(DEFAULT_PACKAGE_ERROR);

        packageCurrencyLimitList.forEach(packageCurrencyLimit -> {
            AgentLimitProfile agentLimitProfile = new AgentLimitProfile();
            agentLimitProfile.setAgentId(agentDetails.getAgentId());
            agentLimitProfile.setCurrencyName(packageCurrencyLimit.getCurrency().getName());
            agentLimitProfile.setDailyTransactionAmount(packageCurrencyLimit.getDailyTransactionAmount());
            agentLimitProfile.setMonthlyTransactionAmount(packageCurrencyLimit.getMonthlyTransactionAmount());
            agentLimitProfile.setWeeklyTransactionAmount(packageCurrencyLimit.getWeeklyTransactionAmount());
            agentLimitProfile.setDailyTransactionCount(packageCurrencyLimit.getDailyTransactionCount());
            agentLimitProfile.setWeeklyTransactionCount(packageCurrencyLimit.getWeeklyTransactionCount());
            agentLimitProfile.setMonthlyTransactionCount(packageCurrencyLimit.getMonthlyTransactionCount());
            agentLimitProfile.setCreateDate(Instant.now());
            agentLimitProfile.setDailyCycleDate(LocalDate.now());
            agentLimitProfile.setWeeklyCycleDate(LocalDate.now().plusDays(7));
            agentLimitProfile.setMonthlyCycleDate(LocalDate.now().plusDays(30));
            agentLimitProfileRepository.save(agentLimitProfile);
        });

    }

    private void validateSchema(RegistrationReq registrationReq) {
        if (registrationReq.getRegistrationType() == RegistrationType.EXISTING_BANK_CUSTOMER) {

            if (registrationReq.getBankCustomerId() == null) {
                throw new BadRequestException(
                        EXISTING_BANK_CUSTOMER_REGISTRATION_REQUIRES_BANK_CUSTOMER_ID);
            }
        }
        if (registrationReq.getRegistrationType() == RegistrationType.NON_EXISTING_BANK_CUSTOMER) {
            if (registrationReq.getFirstName() == null || registrationReq.getLastName() == null) {
                throw new BadRequestException(
                        NON_EXISTING_BANK_CUSTOMER_REGISTRATION_REQUIRE_FIRST_NAME_AND_LAST_NAME);
            }
            if (registrationReq.getBankCustomerId() != null) {
                throw new BadRequestException(
                        NON_EXISTING_BANK_CUSTOMER_REGISTRATION_DOES_NOT_REQUIRE_BANK_CUSTOMER_ID);
            }
            if (registrationReq.getAgentDOB() == null) {
                throw new BadRequestException(DATE_OF_BIRTH_IS_REQUIRED);
            }
            if (registrationReq.getAgentBusinessAddress() == null) {
                throw new BadRequestException(ADDRESS_IS_REQUIRED);
            }
            if (registrationReq.getAgentType() == null) {
                throw new BadRequestException(AGENT_TYPE_IS_REQUIRED);
            }
        }
        if (registrationReq.getIdDocumentImages() == null) {
            throw new BadRequestException(ID_DOCUMENT_IS_REQUIRED);
        }

        if (registrationReq.getAgentType() == AgentType.AGENT_MEMBER) {
            if (registrationReq.getSuperAgentPhone() == null)
                throw new BadRequestException(SUPER_AGENT_PHONE_REQUIRED);
        }

        if (registrationReq.getBusincessType() == BusinessType.SAS || registrationReq.getBusincessType() == BusinessType.SARL) {
            if (registrationReq.getBusinessRegistrationDate() == null) {
                throw new BadRequestException(BUSSINESS_REGISTRATION_DATE_MISSING);
            } else if (registrationReq.getTradeRegistrationNumber() == null) {
                throw new BadRequestException(TRADE_REGISTRATION_NUMBER_MISSING);
            } else if (registrationReq.getTaxPayerNumber() == null) {
                throw new BadRequestException(TAX_PAYER_NUMBER_MISSING);
            }

        }


    }

    private void validateRequest(RegistrationReq registrationReq) {
        validateSchema(registrationReq);
        agentDetailsRepository
                .findByPhoneNo(registrationReq.getPhoneNo())
                .ifPresent(
                        agent -> {

                            throw new BadRequestException(AGENT_ALREADY_EXISTS_WITH_THIS_PHONE_NUMBER, Status.BAD_REQUEST, agent.getStatus().toString());
                        });
        if (registrationReq.getAgentEmailAddress() != null && !registrationReq.getAgentEmailAddress().isEmpty()) {
            agentDetailsRepository
                    .findByAgentEmailAddress(registrationReq.getAgentEmailAddress())
                    .ifPresent(
                            agent -> {
                                throw new BadRequestException(AGENT_ALREADY_EXISTS_WITH_THIS_EMAIL, agent.getStatus().toString());
                            });
            if (keycloakService.isUserExistByLogin(
                    Constants.KEYCLOAK_AGENT_PREFIX + registrationReq.getPhoneNo())) {
                throw new BadRequestException(AGENT_ALREADY_EXISTS_WITH_THIS_PHONE_NUMBER);
            }

        }

        if (registrationReq.getAgentType() == AgentType.AGENT_MEMBER) {
            Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByPhoneNo(registrationReq.getSuperAgentPhone());
            if (!optionalAgentDetails.isPresent()) {
                throw new BadRequestException(INVALID_SUPER_AGENT_DETAILS);
            } else if (optionalAgentDetails.get().getStatus() != AgentStatus.ACTIVE) {
                throw new BadRequestException(INVALID_SUPER_AGENT_STATUS);
            }

            if (registrationReq.getPackageId() == null || registrationReq.getPackageId().toString().isEmpty()) {
                throw new BadRequestException("PACKAGE ID REQUIRED FOR AGENT MEMBER");
            }

            if (registrationReq.getStatus() == null || registrationReq.getStatus().toString().isEmpty()) {
                throw new BadRequestException("STATUS REQUIRED FOR AGENT MEMBER");
            }

            if (registrationReq.getPassword() == null || registrationReq.getPassword().isEmpty()) {
                throw new BadRequestException("PASSWORD FIELD IS REQUIRED FOR AGENT MEMBER");
            }


        }
    }

//    private TermsConditions setAgentTermsAndConditions(RegistrationReq registrationReq) {
//
//        TermsConditionsContent termsConditionsContent = new TermsConditionsContent();
//        termsConditionsContent.setContent(registrationReq.getTcContentPlainText());
//        termsConditionsContent.setName(registrationReq.getTcContentTitle());
//        termsConditionsContent.setLocale(registrationReq.getTcContentLocale());
//        termsConditionsContent.setUrl(registrationReq.getTcContentWebUrl());
//
//        termsAndConditionContentRepository.save(termsConditionsContent);
//
//        // SAve
//
//        TermsConditionsContentDefication termsConditionsContentDefication = new TermsConditionsContentDefication();
//        termsConditionsContentDefication.setTcType(registrationReq.getTcType());
//        termsConditionsContentDefication.setTcOrder(registrationReq.getTcOrder());
//        termsConditionsContentDeficationRepository.save(termsConditionsContentDefication);
//
//
//        TermsConditions termsConditions = new TermsConditions();
//        termsConditions.setName(registrationReq.getTcName());
//        termsConditions.setStatus(registrationReq.getTcStatus());
//        termsConditions.setTcContent(termsConditionsContent);
//        termsConditions.setTcContentDefination(termsConditionsContentDefication);
//        termsConditions.setEffectiveDateTime(registrationReq.getTcEffectiveDateTime());
//        termsConditions.setExpiryDateTime(registrationReq.getTcExpiryDateTime());
//
//        termsAndConditionRepository.save(termsConditions);
//
//        return termsConditions;
//    }

    private void notifyBankCustomerRegistration(AgentDetails agentDetails) {
        MFAChannel mfaChannel = agentDetails.getMFAChannel();
        notificationService.generateMFACodeAndSend(
                agentDetails,
                mfaChannel,
                Constants.EmailTemplate.CUSTOMER_REGISTRATION_PIN,
                Constants.SMSTemplate.CUSTOMER_REGISTRATION_PIN,
                Map.of("name", agentDetails.getFullName()), false, true, true);
    }

    @Transactional
    public void bankCustRegsetPassword(RegistrationSetPasswordReq registrationSetPasswordReq) {
        Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByPhoneNo(registrationSetPasswordReq.getPhoneNumber());
        log.info("Agent Details {}", optionalAgentDetails.get());


        if (!optionalAgentDetails.isPresent()) {
            throw new BadRequestException(INVALID_AGENT);
        }

        if (optionalAgentDetails.get().getStatus() != AgentStatus.PENDING_PASSWORD_RESET) {
            throw new BadRequestException(INVALID_AGENT_STATUS);
        }
        if (!registrationSetPasswordReq
                .getPassword()
                .equals(registrationSetPasswordReq.getConfirmPassword())) {
            throw new BadRequestException(PASSWORD_AND_CONFIRM_PASSWORD_DOES_NOT_MATCH);
        }


        keycloakService.resetKeycloakUserPassword(
                optionalAgentDetails.get().getPhoneNo(), registrationSetPasswordReq.getPassword());
        keycloakService.enableUser(optionalAgentDetails.get());
        if (optionalAgentDetails.get().getAgentType() == AgentType.AGENT_MEMBER) {
            optionalAgentDetails.get().setStatus(AgentStatus.ACTIVE);
        } else {
            optionalAgentDetails.get().setStatus(AgentStatus.AGENT_LINKING_PENDING);
        }
        agentDetailsRepository.save(optionalAgentDetails.get());

    }

    @Transactional
    public void mfaAgentValidation(ValidatePin validatePin) {
        Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByPhoneNo(validatePin.getPhoneNumber());
        if (optionalAgentDetails.isPresent()) {

            boolean isValidated = notificationService.validateMFACode(optionalAgentDetails.get(), validatePin.getMfaCode());
            if (isValidated == true) {
                optionalAgentDetails.get().setStatus(AgentStatus.PENDING_PASSWORD_RESET);
                agentDetailsRepository.save(optionalAgentDetails.get());

            } else {
                throw new BadRequestException(INVALID_MFA_TOKEN);
            }
        } else {
            throw new BadRequestException(INVALID_AGENT);
        }
    }

    @Transactional
    public void resendAgentPin(ResendAgentPin resendAgentPin) {
        Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByPhoneNo(resendAgentPin.getPhoneNumber());
        if (optionalAgentDetails.isPresent()) {

            notificationService.generateMFACodeAndSend(
                    optionalAgentDetails.get(),
                    optionalAgentDetails.get().getMFAChannel(),
                    Constants.EmailTemplate.CUSTOMER_REGISTRATION_PIN,
                    Constants.SMSTemplate.CUSTOMER_REGISTRATION_PIN,
                    Map.of("name", optionalAgentDetails.get().getFullName()), false, true, true);
        } else {
            throw new BadRequestException(INVALID_AGENT);
        }

    }
}
