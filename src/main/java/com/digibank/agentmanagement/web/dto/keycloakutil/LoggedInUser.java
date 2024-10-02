package com.digibank.agentmanagement.web.dto.keycloakutil;

import com.digibank.agentmanagement.domain.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LoggedInUser {
    private String username;
    private String mobileCountryCode;
    private String iamId;
    private UserType userType;
    private String userId;
    private String email;

    public String getMobileNumber() {
        return username.replaceAll("[^0-9]", "");
    }
}
