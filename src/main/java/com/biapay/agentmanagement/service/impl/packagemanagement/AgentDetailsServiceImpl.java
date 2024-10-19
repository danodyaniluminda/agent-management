package com.biapay.agentmanagement.service.impl.packagemanagement;

import com.biapay.agentmanagement.domain.AgentDetails;
import com.biapay.agentmanagement.domain.AgentStatus;
import com.biapay.agentmanagement.exception.InvalidInputException;
import com.biapay.agentmanagement.exception.ObjectNotFoundException;
import com.biapay.agentmanagement.repository.AgentDetailsRepository;
import com.biapay.agentmanagement.service.AgentDetailsService;
import com.biapay.agentmanagement.utils.AgentManagementUtils;
import com.biapay.agentmanagement.utils.ApiError;
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
        if(agentDetails.getAgentStatus() != AgentStatus.ACTIVE){
            throw new ObjectNotFoundException(ApiError.AGENT_NOT_ACTIVE);
        }
        return  agentDetails;
    }

    @Override
    public AgentDetails findByMobileNumber(String mobileNumber) {
        return agentDetailsRepository.findByPhoneNo(mobileNumber).orElseThrow(() -> new ObjectNotFoundException(ApiError.AGENT_NOT_FOUND));
    }
}
