package com.digibank.agentmanagement.mapper.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.UserRole;
import com.digibank.agentmanagement.web.dto.packagemanagement.UserRoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRoleMapper {

    UserRole fromUserRoleDtoToUserRole(UserRoleDto userRoleDto);

    UserRoleDto fromUserRoleToUserRoleDto(UserRole userRole);
}