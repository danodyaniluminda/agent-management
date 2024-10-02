package com.digibank.agentmanagement.web.dto.packagemanagement;

import com.digibank.agentmanagement.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AgentUserDto {

    private Long agentUserId;
    private String name;
    @NonNull
    private String userName;
    @NonNull
    private String email;
    @NonNull
    private String mobileNumber;

    @JsonIgnore
    private String password;

    private UserStatus userStatus;

    private Long userRoleId;
    private UserRoleDto userRole;
}
