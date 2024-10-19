package com.biapay.agentmanagement.service;

import com.biapay.agentmanagement.config.Constants;
import com.biapay.agentmanagement.config.ErrorCodes;
import com.biapay.agentmanagement.domain.*;
import com.biapay.agentmanagement.enums.KYCApprovalStatus;
import com.biapay.agentmanagement.exception.BadRequestException;
import com.biapay.agentmanagement.exception.BIAPayRuntimeException;
import com.biapay.agentmanagement.exception.ObjectNotFoundException;
import com.biapay.agentmanagement.mapper.AgentServiceMapper;
import com.biapay.agentmanagement.mapper.packagemanagement.AgentPackageMapper;
import com.biapay.agentmanagement.repository.AgentDetailsRepository;
import com.biapay.agentmanagement.repository.AgentLinkingRepository;
import com.biapay.agentmanagement.repository.AgentPackageRepository;
import com.biapay.agentmanagement.repository.DocumentRepository;
import com.biapay.agentmanagement.repository.packagemanagement.AgentLimitProfileRepository;
import com.biapay.agentmanagement.repository.packagemanagement.PackageCurrencyLimitRepository;
import com.biapay.agentmanagement.utils.AgentManagementPropertyHolder;
import com.biapay.agentmanagement.utils.AgentManagementUtils;
import com.biapay.agentmanagement.utils.ApiError;
import com.biapay.agentmanagement.web.dto.AgentDetailsDTO;
import com.biapay.agentmanagement.web.dto.AgentLinkingRequestDTO;
import com.biapay.agentmanagement.web.dto.AgentReq;
import com.biapay.agentmanagement.web.dto.agentregistration.agentkyc.AgentKYCApprovalReq;
import com.biapay.agentmanagement.web.dto.agentregistration.agentkyc.AgentKYCDTO;
import com.biapay.agentmanagement.web.dto.agentregistration.agentkyc.AgentKYCResDTO;
import com.biapay.agentmanagement.web.dto.agentregistration.request.AgentProfileUpdateReq;
import com.biapay.agentmanagement.web.dto.agentregistration.response.AgentLinkingResponse;
import com.biapay.agentmanagement.web.dto.agentregistration.response.DashboardSummaryResponse;
import com.biapay.agentmanagement.web.dto.agentregistration.response.WalletBalanceSummary;
import com.biapay.agentmanagement.web.dto.response.AgentDetailsResponse;
import com.biapay.agentmanagement.web.dto.response.TransactionRes;
import com.biapay.agentmanagement.web.dto.wallettransfer.response.CustomerBalanceResponse;
import kong.unirest.HttpResponse;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.biapay.agentmanagement.utils.ApiError.AGENT_NOT_FOUND;
import static com.biapay.agentmanagement.utils.ApiError.DEFAULT_PACKAGE_ERROR;


@Service
@Slf4j
@AllArgsConstructor
public class AgentProfileService {

    private final DocumentRepository documentRepository;
    private final AgentServiceMapper agentServiceMapper;
    private final AgentLinkingRepository agentLinkingRepository;
    private final AgentDetailsRepository agentDetailsRepository;
    private final UtilityService utilityService;
    private final BankAdapterService bankAdapterService;
    private final NotificationService notificationService;
    private final KeycloakService keycloakService;
    private final AgentManagementUtils agentManagementUtils;
    private final AgentPackageMapper agentPackageMapper;
    private final AgentManagementPropertyHolder agentManagementPropertyHolder;
    private final AgentLimitProfileRepository agentLimitProfileRepository;
    private final WalletService walletService;
    private final PackageCurrencyLimitRepository packageCurrencyLimitRepository;
    private final AgentPackageRepository agentPackageRepository;

//    @Transactional
//    public List<BusinessTypeDTO> findAllBusinessTypesByActiveTrue(){
//        List<BusinessType> businessTypeList = businessTypeRepository.findAllByActiveTrue();
//        return agentServiceMapper.fromBusinessesEntityToDTOs(businessTypeList);
//    }
//    @Transactional
//    public List<AgentTypeDTO> findAllAgentTypesByActiveTrue(){
//        List<AgentType> agentTypeList = agentTypeRepository.findAllByActiveTrue();
//        return agentServiceMapper.fromAgentsEntityToDTOs(agentTypeList);
//    }
//    @Transactional
//    public List<TermsConditionsDTO> findAllTermsConditionsByActiveTrue(){
//        List<TermsConditions> termsConditionsList = termsAndConditionRepository.findAllByActiveTrue();
//
//        return agentServiceMapper.fromTermsAndConditionsEntityToDTO(termsConditionsList);
//    }

    public AgentDetailsDTO getAgentDetails(KeycloakAuthenticationToken token, String iAmId) {
//        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
//        String userName  = loggedInUserName.split("_")[1];
//        log.info("User Name : {}", userName);
        AgentDetailsResponse agentDetailsResponse = new AgentDetailsResponse();
        AgentDetails agentDetails = agentDetailsRepository.findByIamId(iAmId).orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        AgentDetailsDTO agentDetailsDTO = agentServiceMapper.fromAgentDetailsToDTO(agentDetails);
        if (agentDetails.getSupervisorAgentId() != null && agentDetails.getSupervisorAgentId().length() > 0) {
            AgentDetails agentDetails1 = agentDetailsRepository.findByIamId(agentDetails.getSupervisorAgentId()).get();
            agentDetailsDTO.setSupervisorAgentEmail(agentDetails1.getEmail());
        }
//
//        agentDetailsDTO.setIdDocuments(
//                agentDetails.getIdDocuments().stream()
//                        .filter(document -> document.getDocumentType() == IDDocumentType.ID_DOCUMENT)
//                        .map(document -> agentServiceMapper.fromDocumentIdInfoEntity(document))
//                        .collect(Collectors.toList()));
//
//        agentDetailsDTO.setSelfieDocument(
//                agentDetails.getSelfieDocuments().stream()
//                        .filter(
//                                document ->
//                                        document.getDocumentType() == IDDocumentType.SELFIE_DOCUMENT)
//                        .map(document -> agentServiceMapper.fromDocumentIdInfoEntity(document))
//                        .findFirst()
//                        .orElse(null));
//
//        agentDetailsDTO.setProofOfAddress(
//                agentDetails.getProofOfAddress().stream()
//                        .filter(document -> document.getDocumentType() == IDDocumentType.ADDRESS_PROOF)
//                        .map(document -> agentServiceMapper.fromDocumentIdInfoEntity(document))
//                        .collect(Collectors.toList()));

        return agentDetailsDTO;
    }

    @Transactional
    public AgentDetailsResponse getAgentDetails(KeycloakAuthenticationToken token, AgentReq transactionReq) {

        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
        String userName = loggedInUserName.split("_")[1];
        log.info("User Name : {}", userName);
        AgentDetailsResponse agentDetailsResponse = new AgentDetailsResponse();
        AgentDetails agentDetails = new AgentDetails();

        if (transactionReq.getPhoneNo() != null && transactionReq.getPhoneNo().length() > 0) {
            agentDetails = agentDetailsRepository.findByPhoneNo(transactionReq.getPhoneNo())
                    .orElseThrow(() -> new BadRequestException("Agent not found"));
        } else if (transactionReq.getEmail() != null && transactionReq.getEmail().length() > 0) {

            agentDetails = agentDetailsRepository.findByAgentEmailAddress(transactionReq.getEmail())
                    .orElseThrow(() -> new BadRequestException("Agent not found"));

        } else {

            throw new BadRequestException("Message Format Error");
        }


        agentDetailsResponse.setAgentName(agentDetails.getAgentName());
        agentDetailsResponse.setPhoneNo(agentDetails.getPhoneNo());
        agentDetailsResponse.setEmail(agentDetails.getEmail());
        agentDetailsResponse.setAgentType(agentDetails.getAgentType());
        agentDetailsResponse.setStatus(agentDetails.isStatus());
        agentDetailsResponse.setRegistrationDateTime(agentDetails.getRegistrationDateTime());
        agentDetailsResponse.setIamId(agentDetails.getIamId());

        return agentDetailsResponse;

    }

    @Transactional
    public TransactionRes validateAgent(AgentReq transactionReq) {

        TransactionRes transactionRes = new TransactionRes();
        if (transactionReq.getEmail().length() == 0 || transactionReq.getPassword().length() == 0) {
            transactionRes = new TransactionRes();
            transactionRes.setGenericResponse(utilityService.setGenericResponse(ErrorCodes.MESSAGE_FORMAT_ERROR_CODE));
            return transactionRes;
        }

//        agentDetailsRepository.findByAgentEmailAddressAndPassword(transactionReq.getEmail() , transactionReq.getPassword())
//                .orElseThrow(() -> new BadRequestException("Invalid Agent Details"));

        transactionRes = new TransactionRes();
        transactionRes.setGenericResponse(utilityService.setGenericResponse(ErrorCodes.PROCESSED_OK_CODE));
        return transactionRes;

    }

    @Transactional
    public void sendNotification(AgentReq transactionReq) {
        if (transactionReq.getAgentUId().length() == 0 || transactionReq.getNotificationType().toString().length() == 0
                || transactionReq.getMfaToken().length() == 0) {
            throw new BadRequestException("Message Field Error");
        }

        Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByIamId(transactionReq.getAgentUId());

        log.info("Received request: {}", optionalAgentDetails);
        log.info("Received request: {}", optionalAgentDetails.isPresent());
        log.info("Received request: {}", optionalAgentDetails.get());

        if (optionalAgentDetails.isPresent() == false) {
            throw new BadRequestException("Invalid Agent UID");
        }

        AgentDetails agentDetails = optionalAgentDetails.get();
        // TODO integration with Notification service for OTP sending

    }

    @Transactional
    public AgentDetailsDTO updateAgentKYC(KeycloakAuthenticationToken token, AgentKYCDTO agentKYCDTO) {

        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
        log.info("User Name : {}", loggedInUserName);
        String loggedInAgentPhoneNo = loggedInUserName.split("_")[1];


        return updateAgentKYC(loggedInAgentPhoneNo, "", agentKYCDTO);


    }

    void validateUpdateKYC(String email, AgentDetails agentDetails) {
        if (email != null
                && !agentDetails.getEmail().equals(email)) {
            agentDetailsRepository
                    .findByAgentEmailAddress(email)
                    .ifPresent(
                            agent -> {
                                throw new BadRequestException(
                                        "AGENT ALREADY EXIST WITH THIS EMAIL");
                            });
        }
    }

    @Transactional
    public AgentDetailsDTO updateAgentKYCBackOffice(String agentPhoneNumber, String loggedInUser,
        AgentKYCDTO agentKYCDTO) {
        return agentDetailsRepository
                .findByPhoneNo(agentPhoneNumber)
                .map(


                        agent -> {
                            if (agent.getAgentStatus() != AgentStatus.AGENT_LINKING_COMPLETED || agent.getAgentStatus() != AgentStatus.ACTIVE) {
                                new BadRequestException(ApiError.INVALID_AGENT_STATUS);
                            }

                            //validateUpdateKYC(agentKYCDTO.getEmailAddress() , agent);

//                            boolean isValidated = notificationService.validateMFACore(agent.getPhoneNo() , agent.getAgentEmailAddress()
//                                    ,agent.getAgentId() , agentKYCDTO.getMfaToken() , "AGENT");
//                            if(!isValidated){
//                                throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
//                            }


//                            agentServiceMapper.updateAgent(agent, agentKYCDTO);
//                            if (agentKYCDTO.getIdDocumentFile().size() > 0) {
//                                agent.getIdDocuments().clear();
//
//                                saveIDDocument(IDDocumentType.ID_DOCUMENT, agentKYCDTO.getIdDocumentFile(), agentKYCDTO.getIdDocumentNumber(), agentKYCDTO.getIdDocumentName(), agentKYCDTO.getIdDocumentExpiryDate(), agent);
//                            }
//
//                            if (agentKYCDTO.getAddressProof().size() > 0) {
//                                agent.getProofOfAddress().clear();
//
//                                saveIDDocument(IDDocumentType.ADDRESS_PROOF, agentKYCDTO.getAddressProof(), null, null, null, agent);
//
//                            }


                            if (agentKYCDTO.getEmail() != null && agentKYCDTO.getEmail().length() > 0) {
                                agent.setEmail(agentKYCDTO.getEmail());
                                keycloakService.updateEmail(agent);
                            }

                            if (agentKYCDTO.getMobileNumber() != null && agentKYCDTO.getMobileNumber().length() > 0) {

                                keycloakService.updatePhoneNumber(agent.getPhoneNo(), agent);
                                agent.setPhoneNo(agentKYCDTO.getMobileNumber());
                            }


                            agent.setAgentStatus(AgentStatus.PENDING_KYC_VERIFICATION);
//                            agent.setKycUpdatedBy(loggedInUser);
//                            agent.setCity(agentKYCDTO.getCity());
//                            agent.setAddress(agentKYCDTO.getAddress());
//                            agent.setAgentDOB(agentKYCDTO.getDateOfBirth());
//                            agent.setLatitude(agentKYCDTO.getLatitude());
//                            agent.setLongitutde(agentKYCDTO.getLongitude());
                            agentDetailsRepository.save(agent);
                            return agent;
                        })
                .map(agent -> agentServiceMapper.fromAgentDetailsToDTO(agent))
                .orElseThrow(() -> new BadRequestException(ApiError.INVALID_AGENT));
    }

    @Transactional
    public AgentDetailsDTO updateAgentKYC(String agentPhoneNumber, String loggedInUser,
        AgentKYCDTO agentKYCDTO) {
        return agentDetailsRepository
                .findByPhoneNo(agentPhoneNumber)
                .map(


                        agent -> {
                            if (agent.getAgentStatus() != AgentStatus.AGENT_LINKING_COMPLETED || agent.getAgentStatus() != AgentStatus.ACTIVE) {
                                new BadRequestException(ApiError.INVALID_AGENT_STATUS);
                            }

//                            //validateUpdateKYC(agentKYCDTO.getEmailAddress() , agent);
//
//                            boolean isValidated = notificationService.validateMFACore(agent.getPhoneNo(), agent.getAgentEmailAddress()
//                                    , agent.getAgentId(), agentKYCDTO.getMfaToken(), "AGENT");
//                            if (!isValidated) {
//                                throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
//                            }


//                            agentServiceMapper.updateAgent(agent, agentKYCDTO);
//                            if (agentKYCDTO.getIdDocumentFile().size() > 0) {
//                                agent.getIdDocuments().clear();
//
//                                saveIDDocument(IDDocumentType.ID_DOCUMENT, agentKYCDTO.getIdDocumentFile(), agentKYCDTO.getIdDocumentNumber(), agentKYCDTO.getIdDocumentName(), agentKYCDTO.getIdDocumentExpiryDate(), agent);
//                            }
//
//                            if (agentKYCDTO.getAddressProof().size() > 0) {
//                                agent.getProofOfAddress().clear();
//
//                                saveIDDocument(IDDocumentType.ADDRESS_PROOF, agentKYCDTO.getAddressProof(), null, null, null, agent);
//
//                            }


                            if (agentKYCDTO.getEmail() != null && agentKYCDTO.getEmail().length() > 0) {
                                agent.setEmail(agentKYCDTO.getEmail());
                                keycloakService.updateEmail(agent);
                            }

                            if (agentKYCDTO.getMobileNumber() != null && agentKYCDTO.getMobileNumber().length() > 0) {

                                keycloakService.updatePhoneNumber(agent.getPhoneNo(), agent);
                                agent.setPhoneNo(agentKYCDTO.getMobileNumber());
                            }


                            agent.setAgentStatus(AgentStatus.PENDING_KYC_VERIFICATION);
//                            agent.setKycUpdatedBy(loggedInUser);
//                            agent.setCity(agentKYCDTO.getCity());
//                            agent.setAddress(agentKYCDTO.getAddress());
//                            agent.setAgentDOB(agentKYCDTO.getDateOfBirth());
//                            agent.setLatitude(agentKYCDTO.getLatitude());
//                            agent.setLongitutde(agentKYCDTO.getLongitude());
                            agentDetailsRepository.save(agent);
                            return agent;
                        })
                .map(agent -> agentServiceMapper.fromAgentDetailsToDTO(agent))
                .orElseThrow(() -> new BadRequestException(ApiError.INVALID_AGENT));
    }


    @Transactional
    public Resource loadFileAsResource(String fileName) {
        try {
            Path fileStorageLocation;
            fileStorageLocation = Paths.get(agentManagementPropertyHolder.getFileStoreBasePath()).toAbsolutePath().normalize();

            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }

    @Transactional
    public AgentDetailsDTO getAgentProfile(KeycloakAuthenticationToken token) {
        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
        log.info("User Name : {}", loggedInUserName);
        String loggedInAgentPhoneNo = loggedInUserName.split("_")[1];

        String uuid = agentManagementUtils.getUUID();

        Optional<AgentDetails> loggedInAgentDetailsOpt = agentDetailsRepository.findByPhoneNo(loggedInAgentPhoneNo);
        if (loggedInAgentDetailsOpt.isPresent()){
            AgentDetails loggedInAgentDetails = loggedInAgentDetailsOpt.get();

            AgentDetailsDTO agentDetailsDTO = agentServiceMapper.fromAgentDetailsToDTO(loggedInAgentDetails);
            agentDetailsDTO.setStatus(loggedInAgentDetails.getStatus());
            agentDetailsDTO.setAgentId(loggedInAgentDetails.getAgentId());
            agentDetailsDTO.setWebsiteLink(loggedInAgentDetails.getWebsiteLink());
            agentDetailsDTO.setCity(loggedInAgentDetails.getCity());
            agentDetailsDTO.setAddress(loggedInAgentDetails.getAddress());
            agentDetailsDTO.setLatitude(loggedInAgentDetails.getLatitude());
            agentDetailsDTO.setLongitude(loggedInAgentDetails.getLongitutde());
            if (loggedInAgentDetails.getSuperAgentId() != null && loggedInAgentDetails.getSuperAgentId().length() > 0) {
                AgentDetails agentDetails = agentDetailsRepository.findByIamId(loggedInAgentDetails.getSuperAgentId()).get();
                agentDetailsDTO.setSuperAgentEmail(agentDetails.getAgentEmailAddress());
            }

            agentDetailsDTO.setIdDocuments(
                    loggedInAgentDetails.getIdDocuments().stream()
                            .filter(document -> document.getDocumentType() == IDDocumentType.ID_DOCUMENT)
                            .map(document -> agentServiceMapper.fromDocumentIdInfoEntity(document))
                            .collect(Collectors.toList()));

            agentDetailsDTO.setSelfieDocument(
                    loggedInAgentDetails.getSelfieDocuments().stream()
                            .filter(
                                    document ->
                                            document.getDocumentType() == IDDocumentType.SELFIE_DOCUMENT)
                            .map(document -> agentServiceMapper.fromDocumentIdInfoEntity(document))
                            .findFirst()
                            .orElse(null));

            agentDetailsDTO.setProofOfAddress(
                    loggedInAgentDetails.getProofOfAddress().stream()
                            .filter(document -> document.getDocumentType() == IDDocumentType.ADDRESS_PROOF)
                            .map(document -> agentServiceMapper.fromDocumentIdInfoEntity(document))
                            .collect(Collectors.toList()));

            try {
                agentDetailsDTO.setAgentLimitProfiles(agentManagementUtils.getAvailableLimits(uuid, loggedInAgentDetails));
            } catch (Exception ex) {
                agentDetailsDTO.setAgentLimitProfiles(null);
            }

            return agentDetailsDTO;
        }
        else{
            throw new BadRequestException(AGENT_NOT_FOUND);
        }
    }


    @Transactional
    public Page<AgentKYCResDTO> getKYCPendingApprovalList(Pageable pageable, String loggedInUserUsername) {
        log.info("logged In UserName : {}", loggedInUserUsername);
        Page<AgentDetails> agentDetails = agentDetailsRepository.findAllByStatus(AgentStatus.PENDING_KYC_VERIFICATION, pageable);
        return agentDetails.map(agent -> getAgentKYCDetails(agent));
    }

    private AgentKYCResDTO getAgentKYCDetails(AgentDetails agent) {
        AgentKYCResDTO agentKYCResDTO = agentServiceMapper.fromAgentDetailsToKYCDTO(agent);
        agentKYCResDTO.setDateOfBirth(agent.getAgentDOB());
        agentKYCResDTO.setEmailAddress(agent.getAgentEmailAddress());
        agentKYCResDTO.setLongitude(agent.getLongitutde());
        agentKYCResDTO.setAgentIamID(agent.getIamId());
        agentKYCResDTO.setAgentStatus(agent.getStatus().toString());
//        agentKYCResDTO.setIdDocuments(
//                agent.getIdDocuments().stream()
//                        .filter(document -> document.getDocumentType() == IDDocumentType.ID_DOCUMENT)
//                        .map(document -> agentServiceMapper.fromDocumentIdInfoEntity(document))
//                        .collect(Collectors.toList()));
        return agentKYCResDTO;
    }

    public void processKYCPendingApprovals(String loggedInUserName, String agentId, AgentKYCApprovalReq agentKYCApprovalReq) {

        log.info("logged In UserName : {}", loggedInUserName);
        log.info("Agent I am  : {}", agentId);
        Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByIamId(agentId);
        log.info("Agent details  : {}", optionalAgentDetails.get());
        if (optionalAgentDetails.isPresent()) {
            if (agentKYCApprovalReq.getKycApprovalStatus() == KYCApprovalStatus.APPROVED) {
                optionalAgentDetails.get().setStatus(AgentStatus.ACTIVE);
                optionalAgentDetails.get().setApprovedBy(loggedInUserName);
                agentDetailsRepository.save(optionalAgentDetails.get());
            } else if (agentKYCApprovalReq.getKycApprovalStatus() == KYCApprovalStatus.PENDING) {
                optionalAgentDetails.get().setStatus(AgentStatus.PENDING_KYC_VERIFICATION);
                optionalAgentDetails.get().setApprovedBy(loggedInUserName);
                agentDetailsRepository.save(optionalAgentDetails.get());
            } else {
                optionalAgentDetails.get().setStatus(AgentStatus.REJECTED_KYC_VERIFICATION);
                optionalAgentDetails.get().setApprovedBy(loggedInUserName);
                agentDetailsRepository.save(optionalAgentDetails.get());
            }
        } else {
            throw new BadRequestException(ApiError.INVALID_AGENT);
        }
    }

    public AgentDetailsDTO updateAgentKYCBackoffice(String loggedInUserName, String agentId, AgentKYCDTO agentKYCDTO) {

        Optional<AgentDetails> optionalAgentDetails = agentDetailsRepository.findByIamId(agentId);
        if (!optionalAgentDetails.isPresent()) {
            throw new BadRequestException(ApiError.INVALID_AGENT);
        }
        return updateAgentKYCBackOffice(optionalAgentDetails.get().getPhoneNo(), loggedInUserName, agentKYCDTO);
    }


    private void validateUpdateRequest(
            AgentProfileUpdateReq agentProfileUpdateReq, AgentDetails agentDetails) {
        if (agentProfileUpdateReq.getEmail() != null
                && !agentDetails.getAgentEmailAddress().equals(agentProfileUpdateReq.getEmail())) {
            agentDetailsRepository
                    .findByAgentEmailAddress(agentProfileUpdateReq.getEmail())
                    .ifPresent(
                            agent -> {
                                throw new BadRequestException(
                                        "AGENT ALREADY EXIST WITH THIS EMAIL");
                            });
        }
        if (agentProfileUpdateReq.getPhoneNumber() != null
                && agentProfileUpdateReq.getPhoneNumberCountryCode() != null
                && (!agentDetails.getPhoneNo().equals(agentProfileUpdateReq.getPhoneNumber())
                || !agentDetails.getPhoneNumberCountryCode()
                .equals(agentProfileUpdateReq.getPhoneNumberCountryCode()))) {
            agentDetailsRepository
                    .findByPhoneNo(agentProfileUpdateReq.getPhoneNumber())
                    .ifPresent(
                            agent -> {
                                throw new BadRequestException(
                                        "AGENT ALREADY EXIST WITH THIS PHONE");
                            });
        }
    }

    private void updateMobile(
            AgentProfileUpdateReq agentProfileUpdateReq, AgentDetails agentDetails) {
        if (agentProfileUpdateReq.getPhoneNumber() != null
                && agentProfileUpdateReq.getPhoneNumberCountryCode() != null
                && (!agentDetails.getPhoneNo().equals(agentProfileUpdateReq.getPhoneNumber())
                || !agentDetails.getPhoneNumberCountryCode()
                .equals(agentProfileUpdateReq.getPhoneNumberCountryCode()))) {
//            log.info(
//                    "Phone is being updated for customer: {}",
//                    agentProfileUpdateReq.getLoggedInUser().getUserId());
            String oldPhoneNumber = agentDetails.getPhoneNo();
            agentDetails.setPhoneNo(agentProfileUpdateReq.getPhoneNumber());
            agentDetails.setPhoneNumberCountryCode(
                    agentProfileUpdateReq.getPhoneNumberCountryCode());
            keycloakService.updatePhoneNumber(oldPhoneNumber, agentDetails);
        }
    }

    private void updateEmail(AgentProfileUpdateReq agentProfileUpdateReq, AgentDetails agentDetails) {
        if (agentProfileUpdateReq.getEmail() != null
                && !agentDetails.getAgentEmailAddress().equals(agentProfileUpdateReq.getEmail())) {

            agentDetails.setAgentEmailAddress(agentProfileUpdateReq.getEmail());
            keycloakService.updateEmail(agentDetails);
        }
    }

    public void updateSelfieDocument(AgentDetails agentDetails, MultipartFile multipartFile) {
        String uuid = agentManagementUtils.saveFile(agentManagementPropertyHolder.getFileStoreBasePath(), multipartFile);


        agentDetails.getSelfieDocuments().stream()
                .filter(document -> document.getDocumentType() == IDDocumentType.SELFIE_DOCUMENT)
                .findFirst()
                .map(
                        document -> {
                            document.setDocumentFileName(uuid);
                            document.setDocumentFileNameOrignal(multipartFile.getOriginalFilename());
                            documentRepository.save(document);
                            return null;
                        })
                .orElseGet(
                        () -> {
                            DocumentIdInfo document =
                                    DocumentIdInfo.builder()
                                            .documentFileNameOrignal(multipartFile.getOriginalFilename())
                                            .documentFileName(uuid)
                                            .documentType(IDDocumentType.SELFIE_DOCUMENT)
                                            .agentDetails(agentDetails)
                                            .build();
                            documentRepository.save(document);
                            return null;
                        });
    }

    @Transactional
    public void updateProfile(KeycloakAuthenticationToken token, AgentProfileUpdateReq agentProfileUpdateReq) {

        String loggedInUserName = AgentManagementUtils.getLoggedInUserUsername(token);
        log.info("User Name : {}", loggedInUserName);
        String loggedInAgentPhoneNo = loggedInUserName.split("_")[1];

        AgentDetails loggedInAgentDetails = agentDetailsRepository.findByPhoneNo(loggedInAgentPhoneNo).get();

        agentDetailsRepository.findByPhoneNo(loggedInAgentPhoneNo)
                .map(
                        agent -> {


                            boolean isValidated = notificationService.validateMFACore(loggedInAgentDetails.getPhoneNo(), loggedInAgentDetails.getAgentEmailAddress()
                                    , loggedInAgentDetails.getAgentId(), agentProfileUpdateReq.getMfaToken(), "AGENT");
                            if (!isValidated) {
                                throw new BadRequestException(ApiError.INVALID_MFA_TOKEN);
                            }
//                            notificationService.validateMFACore(
//                                    agent.getPhoneNumberCountryCode()
//                                            + agent.getPhoneNumber(),
//                                    agentProfileUpdateReq.getMfaToken());


                            validateUpdateRequest(agentProfileUpdateReq, agent);
                            updateEmail(agentProfileUpdateReq, agent);
                            updateMobile(agentProfileUpdateReq, agent);
                            if (agentProfileUpdateReq.getFirstName() != null) {
                                agent.setFirstName(agentProfileUpdateReq.getFirstName());
                            }
                            if (agentProfileUpdateReq.getLastName() != null) {
                                agent.setLastName(agentProfileUpdateReq.getLastName());
                            }
                            if (agentProfileUpdateReq.getSelfieDocumentFile() != null) {
                                updateSelfieDocument(
                                        agent, agentProfileUpdateReq.getSelfieDocumentFile());
                            }
                            agentDetailsRepository.save(agent);
                            return agent;
                        })
                .orElseThrow(() -> new BadRequestException(ApiError.INVALID_AGENT));
    }

    public List<AgentDetailsResponse> getAgentList() {
        List<AgentDetails> agentDetailsList = agentDetailsRepository.findAll();
        List<AgentDetailsResponse> agentDetailsResponses = agentServiceMapper.fromAgentDetailsToAgentDetailsResponses(agentDetailsList);
        return agentDetailsResponses;
    }

    public List<AgentDetailsResponse> getAgentList(Optional<String> iAmIdOptional, Optional<String> emailOptional,
                                                   Optional<String> mobileNumberOptional, Optional<String> statusOptional) {
        List<AgentDetails> agentDetailsList = agentDetailsRepository.findAll(getAgentDetailsQuery(iAmIdOptional, emailOptional, mobileNumberOptional, statusOptional));
        return agentServiceMapper.fromAgentDetailsToAgentDetailsResponses(agentDetailsList);
    }

    public Specification<AgentDetails> getAgentDetailsQuery(Optional<String> iAmIdOptional, Optional<String> emailOptional,
                                                            Optional<String> mobileNumberOptional, Optional<String> statusOptional) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            iAmIdOptional.ifPresent(iAmId -> predicates.add(criteriaBuilder.equal(root.get("iamId"), iAmId)));
            emailOptional.ifPresent(email -> predicates.add(criteriaBuilder.equal(root.get("agentEmailAddress"), email)));
            mobileNumberOptional.ifPresent(mobileNumber -> predicates.add(criteriaBuilder.equal(root.get("phoneNo"), mobileNumber)));
            statusOptional.ifPresent(status -> predicates.add(criteriaBuilder.equal(root.get("status"), AgentStatus.valueOf(status))));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional
    public void getMarkAgentStatus(KeycloakAuthenticationToken token, String iAmId, String agentStatusName) {
        Optional<AgentStatus> agentStatusOptional = Arrays.stream(AgentStatus.values()).filter(as -> as.name().equals(agentStatusName.toUpperCase()))
                .findFirst();
        if (agentStatusOptional.isEmpty()) {
            throw new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND, agentStatusName);
        }
        AgentDetailsResponse agentDetailsResponse = new AgentDetailsResponse();
        AgentDetails agentDetails = agentDetailsRepository.findByIamId(iAmId).orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        agentDetails.setStatus(agentStatusOptional.get());
        agentDetailsRepository.save(agentDetails);
    }

    public DashboardSummaryResponse getDashboardSummary(String uuid, KeycloakAuthenticationToken token, String iAmId) {
        DashboardSummaryResponse dashboardSummaryResponse = new DashboardSummaryResponse();
        AgentDetails agentDetails = agentDetailsRepository.findByIamId(iAmId).orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        dashboardSummaryResponse.setAgentPackageDto(agentPackageMapper.fromAgentPackageToAgentPackageDto(agentDetails.getAgentPackage()));
        dashboardSummaryResponse.setSubAgentsCount(agentDetailsRepository.countBySuperAgentId(agentDetails.getAgentId().toString()));

        List<WalletBalanceSummary> walletBalanceSummaries = new ArrayList<WalletBalanceSummary>();
        List<CustomerBalanceResponse> customerBalanceResponses = walletService.walletBalance(token, "AGENT", agentDetails.getAgentId().toString());
        customerBalanceResponses.forEach(customerBalanceResponse -> {
            WalletBalanceSummary walletBalanceSummary = new WalletBalanceSummary();
            walletBalanceSummary.setWalletBalance(customerBalanceResponse);
            walletBalanceSummary.setLiquidityBalance(agentManagementUtils.getLiquidityBalance(uuid, agentDetails, customerBalanceResponse.getBalance(), customerBalanceResponse.getCurrencyCode()));

            walletBalanceSummaries.add(walletBalanceSummary);

        });
        dashboardSummaryResponse.setWalletBalanceSummaryList(walletBalanceSummaries);
        dashboardSummaryResponse.setAgentLimitProfiles(agentManagementUtils.getAvailableLimits(uuid, agentDetails));
        return dashboardSummaryResponse;
    }

    @Transactional
    public AgentKYCResDTO getKYC(AgentDetails agentDetails) {
        AgentKYCResDTO agentKYCResDTO = getAgentKYCDetails(agentDetails);
        return agentKYCResDTO;

    }

    @Transactional
    public void updateAgentPackage(String username, String agentId, String packageId) {

        AgentDetails agentDetails = agentDetailsRepository.findByIamId(agentId).get();

        List<AgentLimitProfile> agentLimitProfilesList = agentLimitProfileRepository.deleteByAgentId(agentDetails.getAgentId());

        AgentPackage agentPackage = agentPackageRepository.findByPackageId(Long.parseLong(packageId)).get();

        if (agentPackage.getAgentType() != agentDetails.getAgentType())
            throw new BadRequestException("AGENT TYPE MISSMATCHED");

        List<PackageCurrencyLimit> packageCurrencyLimitList = packageCurrencyLimitRepository.findAllByAgentPackage(agentPackage);
        if (packageCurrencyLimitList.isEmpty())
            throw new BadRequestException(DEFAULT_PACKAGE_ERROR);

        packageCurrencyLimitList.forEach(packageCurrencyLimit -> {
            AgentLimitProfile agentLimitProfile = new AgentLimitProfile();
            agentLimitProfile.setAgentId(agentDetails.getAgentId());
            agentLimitProfile.setCurrencyName(packageCurrencyLimit.getCurrency().getName());
            agentLimitProfile.setDailyTransactionAmount(BigDecimal.ZERO);
            agentLimitProfile.setMonthlyTransactionAmount(BigDecimal.ZERO);
            agentLimitProfile.setWeeklyTransactionAmount(BigDecimal.ZERO);
            agentLimitProfile.setDailyTransactionCount(0);
            agentLimitProfile.setWeeklyTransactionCount(0);
            agentLimitProfile.setMonthlyTransactionCount(0);
            agentLimitProfile.setCreateDate(Instant.now());
            agentLimitProfile.setDailyCycleDate(LocalDate.now());
            agentLimitProfile.setWeeklyCycleDate(LocalDate.now().plusDays(7));
            agentLimitProfile.setMonthlyCycleDate(LocalDate.now().plusDays(30));
            agentLimitProfileRepository.save(agentLimitProfile);
        });

        agentDetails.setAgentPackage(agentPackage);
        agentDetailsRepository.save(agentDetails);


    }

    public AgentDetailsDTO getAgentProfileDetails(KeycloakAuthenticationToken token, AgentReq agentReq) {
        String uuid = agentManagementUtils.getUUID();
        Optional<AgentDetails> agentInfo = Optional.empty();
        AgentDetailsDTO agentDetailsDTO = new AgentDetailsDTO();

        if (agentReq.getEmail().length() != 0){
            agentInfo = agentDetailsRepository.findByAgentEmailAddress(agentReq.getEmail());
        }
        if (agentReq.getPhoneNo().length() != 0){
            agentInfo = agentDetailsRepository.findByPhoneNo(agentReq.getPhoneNo());
        }
        log.info("User Name : {}", agentInfo);

        if (agentInfo.isPresent()){
            AgentDetails agentDetails = agentInfo.get();
            agentDetailsDTO = agentServiceMapper.fromAgentDetailsToDTO(agentDetails);

            if (agentDetails.getSuperAgentId() != null && agentDetails.getSuperAgentId().length() > 0) {
                AgentDetails superAgentDetails = agentDetailsRepository.findByIamId(agentDetails.getSuperAgentId()).get();
                agentDetailsDTO.setSuperAgentEmail(superAgentDetails.getAgentEmailAddress());
            }


            agentDetailsDTO.setAgentLimitProfiles(agentManagementUtils.getAvailableLimits(uuid, agentDetails));
        }
        else{
            throw new BadRequestException(AGENT_NOT_FOUND);
        }


        return agentDetailsDTO;

    }
}
