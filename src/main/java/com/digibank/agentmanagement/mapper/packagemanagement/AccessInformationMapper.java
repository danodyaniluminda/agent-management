package com.digibank.agentmanagement.mapper.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.AccessInformation;
import com.digibank.agentmanagement.web.dto.packagemanagement.AccessInformationDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccessInformationMapper {

    AccessInformation fromAccessInformationDtoToAccessInformation(AccessInformationDto accessInformationDto);

    AccessInformationDto fromAccessInformationToAccessInformationDto(AccessInformation accessInformation);
}
