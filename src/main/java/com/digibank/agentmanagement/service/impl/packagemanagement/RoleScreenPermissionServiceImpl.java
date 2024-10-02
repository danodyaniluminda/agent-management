package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.RoleScreenPermission;
import com.digibank.agentmanagement.domain.packagemanagement.Screen;
import com.digibank.agentmanagement.domain.packagemanagement.UserRole;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.mapper.packagemanagement.RoleScreenPermissionMapper;
import com.digibank.agentmanagement.repository.packagemanagement.RoleScreenPermissionRepository;
import com.digibank.agentmanagement.service.packagemanagement.RoleScreenPermissionService;
import com.digibank.agentmanagement.service.packagemanagement.ScreenService;
import com.digibank.agentmanagement.service.packagemanagement.UserRoleService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.web.dto.packagemanagement.RoleScreenPermissionDto;
import com.digibank.agentmanagement.web.dto.packagemanagement.requests.RoleScreenPermissionRequest;
import com.digibank.agentmanagement.utils.ApiError;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RoleScreenPermissionServiceImpl implements RoleScreenPermissionService {

    private final RoleScreenPermissionRepository roleScreenPermissionRepository;
    private final RoleScreenPermissionMapper roleScreenPermissionMapper;

    private final UserRoleService userRoleService;
    private final ScreenService screenService;


    @Override
    public List<RoleScreenPermissionDto> getAll(Long userRoleId) {
        List<RoleScreenPermissionDto> roleScreenPermissions = new ArrayList<>();
        if (userRoleId != null) {
            roleScreenPermissionRepository.findByUserRoleUserRoleId(userRoleId).forEach(roleScreenPermission -> {
                RoleScreenPermissionDto roleScreenPermissionDto = roleScreenPermissionMapper.fromRoleScreenPermissionToRoleScreenPermissionDto(roleScreenPermission);
                roleScreenPermissionDto.setScreenDto(roleScreenPermissionMapper.fromScreenToScreenDTO(roleScreenPermission.getScreen()));
                roleScreenPermissions.add(roleScreenPermissionDto);
            });
        } else {
            roleScreenPermissionRepository.findAll().forEach(roleScreenPermission -> {
                RoleScreenPermissionDto roleScreenPermissionDto = roleScreenPermissionMapper.fromRoleScreenPermissionToRoleScreenPermissionDto(roleScreenPermission);
                roleScreenPermissionDto.setScreenDto(roleScreenPermissionMapper.fromScreenToScreenDTO(roleScreenPermission.getScreen()));
                roleScreenPermissions.add(roleScreenPermissionDto);
            });
        }
        return roleScreenPermissions;
    }

    @Override
    public RoleScreenPermissionDto get(Long roleScreenPermissionId) {
        AgentManagementUtils.isValidId(roleScreenPermissionId);
        RoleScreenPermission roleScreenPermission = roleScreenPermissionRepository.findById(roleScreenPermissionId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        return roleScreenPermissionMapper.fromRoleScreenPermissionToRoleScreenPermissionDto(roleScreenPermission);
    }

    @Override
    @Transactional
    public void addAll(RoleScreenPermissionRequest roleScreenPermissionRequest) {
        UserRole userRole = userRoleService.find(roleScreenPermissionRequest.getUserRoleId());
        if(CollectionUtils.isNotEmpty(roleScreenPermissionRequest.getScreenPermissions())){
            roleScreenPermissionRequest.getScreenPermissions().forEach(screenPermission -> {
                Screen screen = screenService.find(screenPermission.getScreenId());
                RoleScreenPermission roleScreenPermission = new RoleScreenPermission();
                roleScreenPermission.setAdd(screenPermission.getAdd());
                roleScreenPermission.setUpdate(screenPermission.getUpdate());
                roleScreenPermission.setDelete(screenPermission.getDelete());
                roleScreenPermission.setView(screenPermission.getView());
                roleScreenPermission.setList(screenPermission.getList());
                roleScreenPermission.setUserRole(userRole);
                roleScreenPermission.setScreen(screen);
                roleScreenPermissionRepository.save(roleScreenPermission);
            });
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAll(RoleScreenPermissionRequest roleScreenPermissionRequest) {
        UserRole userRole = userRoleService.find(roleScreenPermissionRequest.getUserRoleId());
        roleScreenPermissionRepository.deleteByUserRole(userRole);
        if(CollectionUtils.isNotEmpty(roleScreenPermissionRequest.getScreenPermissions())){
            roleScreenPermissionRequest.getScreenPermissions().forEach(screenPermission -> {
                Screen screen = screenService.find(screenPermission.getScreenId());
                RoleScreenPermission roleScreenPermission = new RoleScreenPermission();
                roleScreenPermission.setAdd(screenPermission.getAdd());
                roleScreenPermission.setUpdate(screenPermission.getUpdate());
                roleScreenPermission.setDelete(screenPermission.getDelete());
                roleScreenPermission.setView(screenPermission.getView());
                roleScreenPermission.setList(screenPermission.getList());
                roleScreenPermission.setUserRole(userRole);
                roleScreenPermission.setScreen(screen);
                roleScreenPermissionRepository.save(roleScreenPermission);
            });
        }
    }
}
