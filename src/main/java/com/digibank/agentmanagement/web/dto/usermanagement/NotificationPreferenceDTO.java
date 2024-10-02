package com.digibank.agentmanagement.web.dto.usermanagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationPreferenceDTO {
    private boolean smsNotification;

    private boolean emailNotification;

    private boolean pushNotification;

}
