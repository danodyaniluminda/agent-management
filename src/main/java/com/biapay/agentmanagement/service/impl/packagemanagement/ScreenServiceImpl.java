package com.biapay.agentmanagement.service.impl.packagemanagement;

import com.biapay.agentmanagement.domain.packagemanagement.Screen;
import com.biapay.agentmanagement.exception.InvalidInputException;
import com.biapay.agentmanagement.exception.ObjectAlreadyExistException;
import com.biapay.agentmanagement.exception.ObjectNotFoundException;
import com.biapay.agentmanagement.mapper.packagemanagement.ScreenMapper;
import com.biapay.agentmanagement.repository.packagemanagement.ScreenRepository;
import com.biapay.agentmanagement.service.packagemanagement.ScreenService;
import com.biapay.agentmanagement.utils.AgentManagementUtils;
import com.biapay.agentmanagement.web.dto.packagemanagement.ScreenDto;
import com.biapay.agentmanagement.utils.ApiError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ScreenServiceImpl implements ScreenService {

    private final ScreenRepository screenRepository;
    private final ScreenMapper screenMapper;

    @Override
    public Screen find(Long screenId) {
        AgentManagementUtils.isValidId(screenId);
        return screenRepository.findById(screenId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public Screen find(String screenId) {
        try {
            return find(Long.parseLong(screenId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }

    @Override
    public Screen findByName(String name) {
        return screenRepository.findByName(name)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
    }

    @Override
    public ScreenDto get(Long screenId) {
        AgentManagementUtils.isValidId(screenId);
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ObjectNotFoundException(ApiError.OBJECT_NOT_FOUND));
        return screenMapper.fromScreenToScreenDto(screen);
    }

    @Override
    public List<ScreenDto> getAll() {
        List<ScreenDto> screens = new ArrayList<>();
        screenRepository.findAll().forEach(screen -> {
            ScreenDto screenDto = screenMapper.fromScreenToScreenDto(screen);
            screens.add(screenDto);
        });
        return screens;
    }

    @Override
    public ScreenDto add(ScreenDto screenRequest) {
        if(screenRepository.findByName(screenRequest.getName()).isPresent()){
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, screenRequest.getName());
        }

        Screen screen = screenMapper.fromScreenDtoToScreen(screenRequest);
        screenRepository.save(screen);
        return screenMapper.fromScreenToScreenDto(screen);
    }

    @Override
    public ScreenDto update(Long screenId, ScreenDto screenRequest) {
        if(screenRepository.findByNameAndScreenIdNot(screenRequest.getName(), screenId).isPresent()){
            throw new ObjectAlreadyExistException(ApiError.OBJECT_ALREADY_EXIST, screenRequest.getName());
        }

        Screen screen = find(screenId);
        screen.setName(screenRequest.getName());
        screen.setDescription(screenRequest.getDescription());
        screenRepository.save(screen);
        return screenMapper.fromScreenToScreenDto(screen);
    }

    @Override
    public void delete(Long screenId) {
        AgentManagementUtils.isValidId(screenId);
        Screen screen = find(screenId);
        screenRepository.delete(screen);
    }

    @Override
    public void delete(String screenId) {
        try {
            delete(Long.parseLong(screenId));
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
    }
}
