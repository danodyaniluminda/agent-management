package com.biapay.agentmanagement.mapper.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.Screen;
import com.biapay.agentmanagement.web.dto.packagemanagement.ScreenDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScreenMapper {

    Screen fromScreenDtoToScreen(ScreenDto screenDto);

    ScreenDto fromScreenToScreenDto(Screen screen);
}