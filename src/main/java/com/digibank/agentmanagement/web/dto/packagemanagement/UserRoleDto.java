package com.digibank.agentmanagement.web.dto.packagemanagement;

import lombok.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDto {

    private Long userRoleId;
    @NonNull
    private String name;
    @NonNull
    private String displayName;
    private String description;
    @NonNull
    private boolean defaultRole;
}
