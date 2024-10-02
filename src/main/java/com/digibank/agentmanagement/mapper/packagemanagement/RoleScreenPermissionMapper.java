package com.digibank.agentmanagement.mapper.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.RoleScreenPermission;
import com.digibank.agentmanagement.domain.packagemanagement.Screen;
import com.digibank.agentmanagement.web.dto.packagemanagement.RoleScreenPermissionDto;
import com.digibank.agentmanagement.web.dto.packagemanagement.ScreenDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleScreenPermissionMapper {

    RoleScreenPermission fromRoleScreenPermissionDtoToRoleScreenPermission(RoleScreenPermissionDto roleScreenPermissionDto);

    RoleScreenPermissionDto fromRoleScreenPermissionToRoleScreenPermissionDto(RoleScreenPermission roleScreenPermission);

    ScreenDto fromScreenToScreenDTO(Screen screen);
}