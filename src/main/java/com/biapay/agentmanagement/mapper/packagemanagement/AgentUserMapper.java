package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.AgentUser;
import com.biapay.agentmanagement.web.dto.packagemanagement.AgentUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgentUserMapper {

    AgentUser fromAgentUserDtoToAgentUser(AgentUserDto agentUserDto);

    AgentUserDto fromAgentUserToAgentUserDto(AgentUser agentUser);
}
