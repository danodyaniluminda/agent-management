package com.biapay.agentmanagement.service.packagemanagement;

import com.biapay.agentmanagement.web.dto.packagemanagement.RoleScreenPermissionDto;
import com.biapay.agentmanagement.web.dto.packagemanagement.requests.RoleScreenPermissionRequest;

import java.util.List;

public interface RoleScreenPermissionService {

    List<RoleScreenPermissionDto> getAll(Long userRoleId);

    RoleScreenPermissionDto get(Long roleScreenPermissionId);

    void addAll(RoleScreenPermissionRequest roleScreenPermissionRequests);

    void updateAll(RoleScreenPermissionRequest roleScreenPermissionRequests);
}
