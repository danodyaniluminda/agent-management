package com.digibank.agentmanagement.controller;

import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.DocumentIdInfo;
import com.digibank.agentmanagement.exception.BadRequestException;
import com.digibank.agentmanagement.repository.DocumentRepository;
import com.digibank.agentmanagement.service.AgentDetailsService;
import com.digibank.agentmanagement.service.AgentProfileService;
import com.digibank.agentmanagement.service.TransactionService;
import com.digibank.agentmanagement.service.packagemanagement.WalletTransferService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.AgentDetailsDTO;
import com.digibank.agentmanagement.web.dto.AgentReq;
import com.digibank.agentmanagement.web.dto.agentregistration.agentkyc.AgentKYCResDTO;
import com.digibank.agentmanagement.web.dto.agentregistration.request.AgentProfileUpdateReq;
import com.digibank.agentmanagement.web.dto.response.AgentDetailsResponse;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.WalletHistoryRequest;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.CustomerBalanceResponse;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.WalletHistoryPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class AgentHomeController {

    // TODO Autowiring making beans
    private final AgentProfileService agentProfileService;

    private final TransactionService transactionService;

    private final WalletTransferService walletTransferService;

    private final DocumentRepository documentRepository;

    private final AgentDetailsService agentDetailsService;

    public AgentHomeController(AgentProfileService agentProfileService, TransactionService transactionService,
                               WalletTransferService walletTransferService, DocumentRepository documentRepository, AgentDetailsService agentDetailsService) {
        this.agentProfileService = agentProfileService;
        this.transactionService = transactionService;
        this.walletTransferService = walletTransferService;
        this.documentRepository = documentRepository;
        this.agentDetailsService = agentDetailsService;
    }

    @Operation(description = "OTP Sending")
    @PostMapping(value = "/api-public/AgentRegistration/sendOTP")
    public ResponseEntity sendOTP(@Valid AgentReq transactionReq) {
        log.info("Received request: {}", transactionReq);
        agentProfileService.sendNotification(transactionReq);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Ping")
    @GetMapping(value = "/api-internal/home/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Welcome To Digibank Agency banking.Date: 28/5/2022");
    }

    @Operation(description = "List of All Agents")
    @GetMapping(value = "/api-internal/backoffice/getAgentsList")
    public ResponseEntity<List<AgentDetailsResponse>> getAgentsList() {
        log.info("Received request");
        List<AgentDetailsResponse> agentDetailsResponse = transactionService.getAgentList();
        return ResponseEntity.ok(agentDetailsResponse);
    }

    @Operation(description = "List of All Agents Member")
    @GetMapping(value = "/api-internal/backoffice/getAgentMemberList")
    public ResponseEntity<List<AgentDetailsResponse>> getAgentMemberList(KeycloakAuthenticationToken token) {
        log.info("Received request");
        List<AgentDetailsResponse> agentDetailsResponse = transactionService.getAgentMemberList(token);
        return ResponseEntity.ok(agentDetailsResponse);
    }

    @Operation(description = "change Agent Member Status")
    @PutMapping(value = "/api-internal/backoffice/changeAgentMemberStatus/{id}/{status}")
    public ResponseEntity changeAgentMemberStatus(KeycloakAuthenticationToken token, @PathVariable String id, @PathVariable String status) {
        log.info("Received request");
        transactionService.changeAgentMemberStatus(token, id, status);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Get Agent Details ")
    @PostMapping(value = "/api-public/Agent/getAgentDetails")
    public ResponseEntity<AgentDetailsResponse> agentDetails(KeycloakAuthenticationToken token, @Valid @RequestBody AgentReq transactionReq) {
        log.info("Received request: {}", transactionReq);
        AgentDetailsResponse agentDetailsResponse = agentProfileService.getAgentDetails(token, transactionReq);
        return ResponseEntity.ok(agentDetailsResponse);
    }

    @Operation(description = "Get Profile ")
    @GetMapping(value = "/api-public/Agent/profile")
    public ResponseEntity<AgentDetailsDTO> agentDetails(KeycloakAuthenticationToken token) {
        AgentDetailsDTO agentDetailsDTO = agentProfileService.getAgentProfile(token);
        return ResponseEntity.ok(agentDetailsDTO);
    }

    @PutMapping(value = "/api-public/Agent/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateProfile(KeycloakAuthenticationToken token, @Valid AgentProfileUpdateReq agentProfileUpdateReq) {
        agentProfileService.updateProfile(token, agentProfileUpdateReq);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Get Agent Wallet Balance ")
    @GetMapping(value = "/api/agencyBanking/agentWalletBalance")
    public ResponseEntity<?> getAgentWalletBalance(KeycloakAuthenticationToken token) {
        List<CustomerBalanceResponse> customerBalanceResponses = walletTransferService.getAgentWalletBalance(token);
        return ResponseEntity.ok(customerBalanceResponses);
    }

    @Operation(description = "Get Agent Wallet History ")
    @GetMapping(value = "/api/agencyBanking/agentWalletHistory")
    public ResponseEntity<?> getAgentWalletHistory(KeycloakAuthenticationToken token, WalletHistoryRequest walletHistoryRequest) {
        log.info("Received request: {}", walletHistoryRequest);
        WalletHistoryPageResponse walletHistoryPageResponse = walletTransferService.AgentWalletHistory(token, walletHistoryRequest);
        return ResponseEntity.ok(walletHistoryPageResponse);
    }

    @Operation(description = "KYC Values")
    @GetMapping(value = "/api/agencyBanking/getKYC")
    public ResponseEntity<AgentKYCResDTO> getKYC(KeycloakAuthenticationToken token) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));
        //AgentKYCResDTO agentKYCResDTO = agentProfileService.updateProfile(agentDetails);
        return ResponseEntity.ok(agentProfileService.getKYC(agentDetails));
    }

    @Operation(description = "Download Documents ")
    @GetMapping(value = "/api/agencyBanking/documents/{documentId}")
    public ResponseEntity downloadDocument(KeycloakAuthenticationToken token, @PathVariable("documentId") String documentId,
                                           HttpServletRequest request) {
        AgentDetails agentDetails = agentDetailsService.findByMobileNumber(AgentManagementUtils.getLoggedInUserUsernameMobileNumber(token));

        Optional<DocumentIdInfo> documentOptional = documentRepository.findByDocumentFileNameAndAgentDetails(documentId, agentDetails);
        //return ResponseEntity.ok();
        return documentOptional.map(document -> {
            Resource resource = agentProfileService.loadFileAsResource(document.getDocumentFileName());
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                log.warn("Could not determine file type.");
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getDocumentFileNameOrignal() + "\"")
                    .body(resource);
        }).orElseThrow(() -> new BadRequestException("INVALID_DOCUMENT"));

    }

    @Operation(description = "Get Agent Profile by Phone or Email")
    @GetMapping(value = "/api-public/Agent/profile-details")
    public ResponseEntity<AgentDetailsDTO> agentProfileDetails(KeycloakAuthenticationToken token, @Valid @RequestBody AgentReq agentReq) {
        AgentDetailsDTO agentDetailsDTO = agentProfileService.getAgentProfileDetails(token, agentReq);
        return ResponseEntity.ok(agentDetailsDTO);
    }

    @Operation(description = "Get Agent Profile by Phone or Email")
    @GetMapping(value = "/error")
    public ResponseEntity<?> error(HashMap<String, String> params) {
        return ResponseEntity.ok(params);
    }
}
