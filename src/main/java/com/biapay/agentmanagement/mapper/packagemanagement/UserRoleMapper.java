package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.UserRole;
import com.biapay.agentmanagement.web.dto.packagemanagement.UserRoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRoleMapper {

    UserRole fromUserRoleDtoToUserRole(UserRoleDto userRoleDto);

    UserRoleDto fromUserRoleToUserRoleDto(UserRole userRole);
}