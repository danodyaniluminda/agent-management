package com.digibank.agentmanagement.service.impl.packagemanagement;

import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.AgentStatus;
import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.exception.ObjectNotFoundException;
import com.digibank.agentmanagement.repository.AgentDetailsRepository;
import com.digibank.agentmanagement.service.AgentDetailsService;
import com.digibank.agentmanagement.utils.AgentManagementUtils;
import com.digibank.agentmanagement.utils.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AgentDetailsServiceImpl implements AgentDetailsService {

    @Autowired
    private AgentDetailsRepository agentDetailsRepository;

    @Autowired
    public AgentDetailsServiceImpl(AgentDetailsRepository agentDetailsRepository) {
        this.agentDetailsRepository = agentDetailsRepository;
    }

    @Override
    public AgentDetails get(Long agentId) {
        AgentManagementUtils.isValidId(agentId);
        return agentDetailsRepository.findById(agentId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public AgentDetails get(String agentIdString) {
        try {
            long agentId = Long.parseLong(agentIdString);
            return get(agentId);
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public AgentDetails findByIAmId(String iAmId) {
        return agentDetailsRepository.findByIamId(iAmId).orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public AgentDetails findActiveAgentsByMobileNumber(String mobileNumber) {
        AgentDetails agentDetails = agentDetailsRepository.findByPhoneNo(mobileNumber).orElseThrow(() -> new ObjectNotFoundException(ApiError.AGENT_NOT_FOUND));
        if(agentDetails.getStatus() != AgentStatus.ACTIVE){
            throw new ObjectNotFoundException(ApiError.AGENT_NOT_ACTIVE);
        }
        return  agentDetails;
    }

    @Override
    public AgentDetails findByMobileNumber(String mobileNumber) {
        return agentDetailsRepository.findByPhoneNo(mobileNumber).orElseThrow(() -> new ObjectNotFoundException(ApiError.AGENT_NOT_FOUND));
    }
}
