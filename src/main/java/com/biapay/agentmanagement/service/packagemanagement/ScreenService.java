package com.biapay.agentmanagement.service.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.Screen;
import com.biapay.agentmanagement.web.dto.packagemanagement.ScreenDto;

import java.util.List;

public interface ScreenService {

    Screen find(Long screenId);

    Screen find(String screenId);

    Screen findByName(String userName);

    ScreenDto get(Long screenId);

    List<ScreenDto> getAll();

    ScreenDto add(ScreenDto screenRequest);

    ScreenDto update(Long screenId, ScreenDto screenRequest);

    void delete(Long screenId);

    void delete(String screenId);
}
