package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.service.BackOfficeAdapterService;
import com.digibank.agentmanagement.service.packagemanagement.BackOfficeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BackOfficeServiceImpl implements BackOfficeService {

    private final BackOfficeAdapterService backOfficeAdapterService;

    @Override
    public void AgentRegistrationBackoffice(AgentDetails agentDetails) {

        backOfficeAdapterService.AgentRegistrationBackOfficeCall(agentDetails);

    }
}
