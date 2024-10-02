package com.digibank.agentmanagement.web.dto.packagemanagement.requests;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserRoleRequest {

    private Long userRoleId;
    @NonNull
    private String name;
    @NonNull
    private String displayName;
    private String description;
    @NonNull
    private boolean defaultRole;
}
