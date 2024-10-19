package com.biapay.agentmanagement.web.dto.packagemanagement.requests;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoleScreenPermissionRequest {

    @NonNull
    private Long userRoleId;
    List<ScreenPermission> screenPermissions;
}
