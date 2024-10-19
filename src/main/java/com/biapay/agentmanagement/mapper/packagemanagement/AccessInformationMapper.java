package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.AccessInformation;
import com.biapay.agentmanagement.web.dto.packagemanagement.AccessInformationDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccessInformationMapper {

    AccessInformation fromAccessInformationDtoToAccessInformation(AccessInformationDto accessInformationDto);

    AccessInformationDto fromAccessInformationToAccessInformationDto(AccessInformation accessInformation);
}
