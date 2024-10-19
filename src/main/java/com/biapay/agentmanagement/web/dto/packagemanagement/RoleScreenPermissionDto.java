package com.biapay.agentmanagement.web.dto.packagemanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoleScreenPermissionDto {

    private Long roleScreenPermissionId;
    private Boolean add;
    private Boolean update;
    private Boolean delete;
    private Boolean view;
    private Boolean list;
    private UserRoleDto userRoleDto;
    private ScreenDto screenDto;
}
