package com.digibank.agentmanagement.controller.packagemanagement;

import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.service.packagemanagement.ScreenService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.assembler.packagemanagement.ScreenModelAssembler;
import com.digibank.agentmanagement.web.dto.packagemanagement.ScreenDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api-internal/screens")
public class ScreenController {

    private final ScreenService screenService;
    private final ScreenModelAssembler screenModelAssembler;

    @Operation(description = "Get All screens")
    @GetMapping(value = "")
    public CollectionModel<EntityModel<ScreenDto>> getAll() {
        log.info("Getting all Screens");

        List<EntityModel<ScreenDto>> screens = screenService.getAll().stream()
                .map(screenModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(screens, linkTo(methodOn(ScreenController.class).getAll()).withSelfRel());
    }

    @Operation(description = "Get Screen")
    @GetMapping(value = "/{screenId}")
    public EntityModel<ScreenDto> get(@PathVariable Long screenId) {
        log.info("Getting Screen: {}", screenId);
        if (screenId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        return screenModelAssembler.toModel(screenService.get(screenId));
    }

    @Operation(description = "Create Screen")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ScreenDto>> create(@RequestBody @Valid ScreenDto screenRequest) {
        log.info("Add new Screen: {}", screenRequest);
        if (screenRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        ScreenDto screenDto = screenService.add(screenRequest);
        return ResponseEntity.created(linkTo(methodOn(ScreenController.class)
                        .get(screenDto.getScreenId())).toUri())
                .body(screenModelAssembler.toModel(screenDto));
    }

    @Operation(description = "Update Screen")
    @PutMapping(value = "/{screenId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ScreenDto>> walletAccountOpening(@PathVariable Long screenId,
                                                                       @RequestBody @Valid ScreenDto screenRequest) {
        log.info("Update Screen: {} with : {}", screenId, screenRequest);
        if (screenId == null || screenRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        if (!screenRequest.getScreenId().equals(screenId)) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        ScreenDto screenDto = screenService.update(screenId, screenRequest);
        return ResponseEntity.created(linkTo(methodOn(ScreenController.class)
                        .get(screenDto.getScreenId())).toUri())
                .body(screenModelAssembler.toModel(screenDto));
    }

    @Operation(description = "Delete Screen")
    @DeleteMapping(value = "/{screenId}")
    public ResponseEntity<?> delete(@PathVariable Long screenId) {
        log.info("Delete Screen: {}", screenId);
        if (screenId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        screenService.delete(screenId);
        return ResponseEntity.noContent().build();
    }
}
