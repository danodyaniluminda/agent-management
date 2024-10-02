package com.digibank.agentmanagement.controller.packagemanagement;

import com.digibank.agentmanagement.exception.InvalidInputException;
import com.digibank.agentmanagement.service.packagemanagement.CurrencyService;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.web.assembler.packagemanagement.CurrencyModelAssembler;
import com.digibank.agentmanagement.web.dto.packagemanagement.CurrencyDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api-internal/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final CurrencyModelAssembler currencyModelAssembler;

    @Operation(description = "Get All Currencies")
    @GetMapping(value = "")
    public CollectionModel<EntityModel<CurrencyDto>> getAll() {
        log.info("Getting all AgentUsers");

        List<EntityModel<CurrencyDto>> currencies = currencyService.getAll().stream()
                .map(currencyModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(currencies, linkTo(methodOn(CurrencyController.class).getAll()).withSelfRel());
    }

    @Operation(description = "Get Currency")
    @GetMapping(value = "/{currencyId}")
    public EntityModel<CurrencyDto> get(@PathVariable Long currencyId) {
        log.info("Getting Currency: {}", currencyId);
        if (currencyId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        return currencyModelAssembler.toModel(currencyService.get(currencyId));
    }

    @Operation(description = "Create Currency")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CurrencyDto>> create(@RequestBody @Valid CurrencyDto currencyRequest) {
        log.info("Add new RoleUser: {}", currencyRequest);
        if (currencyRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        CurrencyDto currencyDto = currencyService.add(currencyRequest);
        return ResponseEntity.created(linkTo(methodOn(CurrencyController.class)
                        .get(currencyDto.getCurrencyId())).toUri())
                .body(currencyModelAssembler.toModel(currencyDto));
    }

    @Operation(description = "Update Currency")
    @PutMapping(value = "/{currencyId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CurrencyDto>> walletAccountOpening(@PathVariable Long currencyId,
                                                                         @RequestBody @Valid CurrencyDto currencyRequest) {
        log.info("Update AgentUser: {} with : {}", currencyId, currencyRequest);
        if (currencyId == null || currencyRequest == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        if (!currencyRequest.getCurrencyId().equals(currencyId)) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        CurrencyDto CurrencyDto = currencyService.update(currencyId, currencyRequest);
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(methodOn(AgentUserController.class)
                        .get(CurrencyDto.getCurrencyId())).toUri())
                .body(currencyModelAssembler.toModel(CurrencyDto));
    }

    @Operation(description = "Create Currency")
    @DeleteMapping(value = "/{currencyId}")
    public ResponseEntity<?> delete(@PathVariable Long currencyId) {
        log.info("Delete AgentUser: {}", currencyId);
        if (currencyId == null) {
            throw new InvalidInputException(ApiError.INVALID_INPUT);
        }
        currencyService.delete(currencyId);
        return ResponseEntity.noContent().build();
    }
}
