package com.biapay.agentmanagement.controller;


import com.biapay.agentmanagement.service.AgentProfileService;
import com.biapay.agentmanagement.service.AgentRegistrationService;
import com.biapay.agentmanagement.web.dto.AgentDetailsDTO;
import com.biapay.agentmanagement.web.dto.AgentLinkingRequestDTO;
import com.biapay.agentmanagement.web.dto.AgentReq;
import com.biapay.agentmanagement.web.dto.RegistrationRes;
import com.biapay.agentmanagement.web.dto.agentregistration.agentkyc.AgentKYCDTO;
import com.biapay.agentmanagement.web.dto.agentregistration.request.RegistrationReq;
import com.biapay.agentmanagement.web.dto.agentregistration.request.RegistrationSetPasswordReq;
import com.biapay.agentmanagement.web.dto.agentregistration.request.ResendAgentPin;
import com.biapay.agentmanagement.web.dto.agentregistration.request.ValidatePin;
import com.biapay.agentmanagement.web.dto.agentregistration.response.AgentLinkingResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class RegistrationController {


    private final AgentRegistrationService agentRegistrationService;
    private final AgentProfileService agentProfileService;

    RegistrationController(AgentRegistrationService agentRegistrationService,
                           AgentProfileService agentProfileService
    ) {
        this.agentRegistrationService = agentRegistrationService;
        this.agentProfileService = agentProfileService;
    }

    @Operation(description = "Register an individual or non-individual agent")
    @PostMapping(
            value = "/api-public/registration/agentRegistration",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RegistrationRes> registerAgent(@Valid RegistrationReq registrationReq) {
        log.info("Received request: {}", registrationReq);
        RegistrationRes res = agentRegistrationService.registerAgent(registrationReq);
        return ResponseEntity.ok(res);
    }


    @Operation(description = "this Api set password of a agent ")
    @PostMapping(value = "/api-public/registration/setPassword")
    public ResponseEntity setPassword(
            @Valid @RequestBody RegistrationSetPasswordReq registrationSetPasswordReq) {
        agentRegistrationService.bankCustRegsetPassword(registrationSetPasswordReq);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "This Api validate the MFA Token ")
    @PostMapping(value = "/api-public/registration/validatePin")
    public ResponseEntity MFAValidation(
            @Valid @RequestBody ValidatePin validatePin) {
        agentRegistrationService.mfaAgentValidation(validatePin);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "This Api resend the MFA Token ")
    @PostMapping(value = "/api-public/registration/resendPin")
    public ResponseEntity resendMFA(
            @Valid @RequestBody ResendAgentPin resendAgentPin) {
        agentRegistrationService.resendAgentPin(resendAgentPin);
        return ResponseEntity.ok().build();
    }


    @Operation(description = "Update Agent KYC")
    @PostMapping(
            value = "/api-public/AgentRegistration/agentKYC")
    public ResponseEntity updateAgentKYC(KeycloakAuthenticationToken token
            , @Valid AgentKYCDTO agentKYCDTO) {
        log.info("Received request");
        agentProfileService.updateAgentKYC(token, agentKYCDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Update Agent KYC")
    @GetMapping(
            value = "/api-public/AgentRegistration/agentKYC")
    public ResponseEntity getKYCApprovelList(KeycloakAuthenticationToken token
            , @Valid AgentKYCDTO agentKYCDTO) {
        log.info("Received request");
        agentProfileService.updateAgentKYC(token, agentKYCDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(description = "Get Agent KYC")
    @GetMapping(
            value = "/api-public/AgentRegistration/agentKYC/{agentId}")
    public ResponseEntity<AgentDetailsDTO> getAgentKYC(KeycloakAuthenticationToken token, @Valid @PathVariable String agentId) {
        log.info("Get agent KYC request");
        AgentDetailsDTO agentDetailsDTO = agentProfileService.getAgentDetails(token, agentId);
        return ResponseEntity.ok(agentDetailsDTO);
    }

}
