package com.digibank.agentmanagement.repository.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.RoleScreenPermission;
import com.digibank.agentmanagement.domain.packagemanagement.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleScreenPermissionRepository extends JpaRepository<RoleScreenPermission, Long> {

    List<RoleScreenPermission> findByUserRoleName(String userRoleName);

    List<RoleScreenPermission> findByScreenName(String ScreenName);

    List<RoleScreenPermission> findByUserRoleNameAndScreenName(String userRoleName, String screenName);

    List<RoleScreenPermission> findByUserRoleUserRoleId(Long userRoleId);

    List<RoleScreenPermission> findByUserRoleUserRoleIdAndScreenScreenId(String userRoleId, String screenId);

    void deleteByUserRole(UserRole userRole);
}
