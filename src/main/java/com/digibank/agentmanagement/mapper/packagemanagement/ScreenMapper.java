package com.digibank.agentmanagement.mapper.packagemanagement;

import com.digibank.agentmanagement.domain.packagemanagement.Screen;
import com.digibank.agentmanagement.web.dto.packagemanagement.ScreenDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScreenMapper {

    Screen fromScreenDtoToScreen(ScreenDto screenDto);

    ScreenDto fromScreenToScreenDto(Screen screen);
}