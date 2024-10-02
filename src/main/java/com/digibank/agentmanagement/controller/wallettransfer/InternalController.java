package com.digibank.agentmanagement.controller.wallettransfer;

import com.digibank.agentmanagement.service.UserManagementAdapterService;
import com.digibank.agentmanagement.service.packagemanagement.WalletTransferService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.wallettransfer.request.WalletToWalletRequest;
import com.digibank.agentmanagement.web.dto.wallettransfer.response.WalletToWalletResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/internal")
public class InternalController {

    private final WalletTransferService walletTransferService;
    private final AgentManagementUtils agentManagementUtils;
    private final UserManagementAdapterService userManagementAdapterService;

    @Operation(description = "Wallet To Wallet")
    @PostMapping(value = "/walletToWallet")
    public ResponseEntity<?> walletToWallet(KeycloakAuthenticationToken token, @RequestBody @Valid WalletToWalletRequest walletToWalletRequest) {
        String uuid = agentManagementUtils.getUUID();
        log.info("{} - Received request: {}", uuid, walletToWalletRequest);
        WalletToWalletResponse walletToWalletResponse = walletTransferService.walletToWalletInternal(uuid, token, walletToWalletRequest);
        return ResponseEntity.ok(walletToWalletResponse);
    }
}
