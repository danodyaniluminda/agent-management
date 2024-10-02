package com.digibank.agentmanagement.web.dto.packagemanagement.requests;

import com.digibank.agentmanagement.enums.UserStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AgentUserRequest {

    private Long agentUserId;
    private String name;
    @NonNull private String userName;
    @NonNull private String email;
    @NonNull private String mobileNumber;
    @NonNull private String password;
    @NonNull private UserStatus userStatus;
    @NonNull private Long userRoleId;
}
