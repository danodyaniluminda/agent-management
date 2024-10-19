package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.RoleScreenPermission;
import com.biapay.agentmanagement.domain.packagemanagement.Screen;
import com.biapay.agentmanagement.web.dto.packagemanagement.RoleScreenPermissionDto;
import com.biapay.agentmanagement.web.dto.packagemanagement.ScreenDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleScreenPermissionMapper {

    RoleScreenPermission fromRoleScreenPermissionDtoToRoleScreenPermission(RoleScreenPermissionDto roleScreenPermissionDto);

    RoleScreenPermissionDto fromRoleScreenPermissionToRoleScreenPermissionDto(RoleScreenPermission roleScreenPermission);

    ScreenDto fromScreenToScreenDTO(Screen screen);
}