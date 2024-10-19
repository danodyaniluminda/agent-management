package com.biapay.agentmanagement.controller.backoffice;


import com.biapay.agentmanagement.domain.DocumentIdInfo;
import com.biapay.agentmanagement.exception.BadRequestException;
import com.biapay.agentmanagement.exception.InvalidInputException;
import com.biapay.agentmanagement.repository.DocumentRepository;
import com.biapay.agentmanagement.service.AgentProfileService;
import com.biapay.agentmanagement.service.AgentRegistrationService;
import com.biapay.agentmanagement.service.TransactionService;
import com.biapay.agentmanagement.utils.AgentManagementUtils;
import com.biapay.agentmanagement.utils.ApiError;
import com.biapay.agentmanagement.web.dto.AgentDetailsDTO;
import com.biapay.agentmanagement.web.dto.TransactionDto;
import com.biapay.agentmanagement.web.dto.agentregistration.agentkyc.AgentKYCApprovalReq;
import com.biapay.agentmanagement.web.dto.agentregistration.agentkyc.AgentKYCDTO;
import com.biapay.agentmanagement.web.dto.agentregistration.agentkyc.AgentKYCResDTO;
import com.biapay.agentmanagement.web.dto.agentregistration.response.DashboardSummaryResponse;
import com.biapay.agentmanagement.web.dto.response.AgentDetailsResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
//@RequestMapping("/api")
public class BackOfficeAgentController {

    private final AgentRegistrationService agentRegistrationService;
    private final AgentProfileService agentProfileService;
    private final AgentManagementUtils agentManagementUtils;
    private final DocumentRepository documentRepository;

    private final TransactionService transactionService;

    @Operation(description = "Get All Agents")
    @GetMapping(value = "/api-backoffice/agents")
    public ResponseEntity<List<AgentDetailsResponse>> getAllAgents(KeycloakAuthenticationToken token, @RequestParam("iAmId") Optional<String> iAmIdOptional,
                                                                   @RequestParam("email") Optional<String> emailOptional,
                                                                   @RequestParam("mobileNumber") Optional<String> mobileNumberOptional,
                                                                   @RequestParam("agentStatus") Optional<String> agentStatusOptional) {
        List<AgentDetailsResponse> agentDetailsResponse = agentProfileService.getAgentList(iAmIdOptional, emailOptional, mobileNumberOptional, agentStatusOptional);
        return ResponseEntity.ok(agentDetailsResponse);
    }

    @Operation(description = "Get Agent Details ")
    @GetMapping(value = "/api-backoffice/agents/getAgentDetails")
    public ResponseEntity<AgentDetailsDTO> agentDetails(KeycloakAuthenticationToken token, @RequestParam("iAmId") String iAmId) {
        if (StringUtils.isEmpty(iAmId)) {
            throw new InvalidInputException(ApiError.INVALID_INPUT_PARAMETER);
        }
        AgentDetailsDTO agentDetailsResponse = agentProfileService.getAgentDetails(token, iAmId);
        return ResponseEntity.ok(agentDetailsResponse);
    }

    @Operation(description = "Get Profile")
    @GetMapping(value = "/api-backoffice/agents/markStatus")
    public ResponseEntity<?> agentDetails(KeycloakAuthenticationToken token, @RequestParam("iAmId") String iAmId,
                                          @RequestParam("agentStatus") String agentStatus) {
        agentProfileService.getMarkAgentStatus(token, iAmId, agentStatus);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/api-backoffice/agents/dashboardSummary")
    public ResponseEntity<DashboardSummaryResponse> dashboardSummary(KeycloakAuthenticationToken token, @RequestParam("iAmId") String iAmId
    ) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}, iAmId: {}", uuid, iAmId);
        DashboardSummaryResponse dashboardSummaryResponse = agentProfileService.getDashboardSummary(uuid, token, iAmId);
        return ResponseEntity.ok(dashboardSummaryResponse);
    }

    @Operation(description = "KYC Pending Approvel List")
    @GetMapping(value = "/api-backoffice/agent/getKYCList")
    public ResponseEntity<Page<AgentKYCResDTO>> getKYCList(KeycloakAuthenticationToken token, Pageable pageable) {
        Page<AgentKYCResDTO> agentKYCDTOS = agentProfileService.getKYCPendingApprovalList(pageable, AgentManagementUtils.getLoggedInUser(token).getUsername());
        return ResponseEntity.ok(agentKYCDTOS);
    }

    @Operation(description = "KYC Approvel/ rejection")
    @PutMapping(value = "/api-backoffice/agent/KYCApprovel/{agentId}")
    public ResponseEntity KYCApprovel(KeycloakAuthenticationToken token, @PathVariable("agentId") String agentId, @RequestBody AgentKYCApprovalReq agentKYCApprovalReq) {
        agentProfileService.processKYCPendingApprovals(AgentManagementUtils.getLoggedInUser(token).getUsername(), agentId, agentKYCApprovalReq);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Update Agent KYC")
    @PutMapping(value = "/api-backoffice/agent/UpdateKYC/{agentId}")
    public ResponseEntity<AgentDetailsDTO> UpdateAgentKYC(KeycloakAuthenticationToken token, @PathVariable("agentId") String agentId, AgentKYCDTO agentKYCDTO) {
        AgentDetailsDTO agentDetailsDTO = agentProfileService.updateAgentKYCBackoffice(AgentManagementUtils.getLoggedInUser(token).getUsername(), agentId, agentKYCDTO);
        return ResponseEntity.ok(agentDetailsDTO);
    }

    @Operation(description = "Download Documents ")
    @GetMapping(
            value = "/api-backoffice/agent/documents/{documentId}")
    public ResponseEntity downloadDocument(KeycloakAuthenticationToken token, @PathVariable("documentId") String documentId, HttpServletRequest request) {


        Optional<DocumentIdInfo> documentOptional =
                documentRepository.findByDocumentFileName(
                        documentId);
        //return ResponseEntity.ok();
        return documentOptional
                .map(
                        document -> {
                            Resource resource =
                                    agentProfileService.loadFileAsResource(document.getDocumentFileName());
                            String contentType = null;
                            try {
                                contentType =
                                        request.getServletContext()
                                                .getMimeType(resource.getFile().getAbsolutePath());
                            } catch (IOException ex) {
                                log.warn("Could not determine file type.");
                            }
                            if (contentType == null) {
                                contentType = "application/octet-stream";
                            }
                            return ResponseEntity.ok()
                                    .contentType(MediaType.parseMediaType(contentType))
                                    .header(
                                            HttpHeaders.CONTENT_DISPOSITION,
                                            "attachment; filename=\""
                                                    + document.getDocumentFileNameOrignal()
                                                    + "\"")
                                    .body(resource);
                        })
                .orElseThrow(() -> new BadRequestException("INVALID_DOCUMENT"));


    }


    @Operation(description = "update Agent Package")
    @PutMapping(value = "/api-backoffice/agent/updatePackage/{agentId}/{packageId}")
    public ResponseEntity updatePackage(KeycloakAuthenticationToken token, @PathVariable("agentId") String agentId, @PathVariable("packageId") String packageId) {
        agentProfileService.updateAgentPackage(AgentManagementUtils.getLoggedInUser(token).getUsername(), agentId, packageId);
        return ResponseEntity.ok().build();
    }


    @Operation(description = "Get Transaction List ")
    @GetMapping(value = "/api-backoffice/transactions")
    public ResponseEntity<Page<TransactionDto>> getTransactions(KeycloakAuthenticationToken token, @RequestParam("page") int page, @RequestParam("size") int size) {


        return ResponseEntity.ok(transactionService.getLastDayTransactions(page, size));
    }
}
