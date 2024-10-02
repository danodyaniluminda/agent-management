package com.digibank.agentmanagement.controller.packagemanagement;

import com.digibank.agentmanagement.exception.*;
import com.digibank.agentmanagement.utils.ErrorConstants;
import com.digibank.agentmanagement.web.dto.response.AgentManagementErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;

import java.net.URI;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class AgentManagementRestControllerAdvice {

    @ExceptionHandler(value = {InvalidAuthenticationTokenException.class})
    public ResponseEntity<AgentManagementErrorResponse> handleInvalidAuthenticationTokenException(InvalidAuthenticationTokenException ex) {
        log.error("Invalid Authentication Token Exception: {}", ex.getDetail(), ex);
        return new ResponseEntity<>(
                createAgentManagementErrorResponse(ex.getType(), ex.getTitle(), ex.getStatus(), ex.getDetail()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {InvalidInputException.class})
    public ResponseEntity<AgentManagementErrorResponse> handleInvalidInputException(InvalidInputException ex) {
        log.error("InvalidInputException: {}", ex.getDetail(), ex);
        return new ResponseEntity<>(
                createAgentManagementErrorResponse(ex.getType(), ex.getTitle(), ex.getStatus(), ex.getDetail()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ObjectNotFoundException.class})
    public ResponseEntity<AgentManagementErrorResponse> handleObjectNotFoundException(ObjectNotFoundException ex) {
        log.error("ObjectNotFoundException: {}", ex.getDetail(), ex);
        return new ResponseEntity<>(
                createAgentManagementErrorResponse(ex.getType(), ex.getTitle(), ex.getStatus(), ex.getDetail()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InvalidObjectStateException.class})
    public ResponseEntity<AgentManagementErrorResponse> handleInvalidObjectStateException(InvalidObjectStateException ex) {
        log.error("InvalidObjectStateException: {}", ex.getDetail(), ex);
        return new ResponseEntity<>(
                createAgentManagementErrorResponse(ex.getType(), ex.getTitle(), ex.getStatus(), ex.getDetail()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ObjectAlreadyExistException.class})
    public ResponseEntity<AgentManagementErrorResponse> handleObjectAlreadyExistException(ObjectAlreadyExistException ex) {
        log.error("ObjectNotFoundException: {}", ex.getDetail(), ex);
        return new ResponseEntity<>(
                createAgentManagementErrorResponse(ex.getType(), ex.getTitle(), ex.getStatus(), ex.getDetail()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<AgentManagementErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("AccessDeniedException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createAgentManagementErrorResponse(ErrorConstants.DEFAULT_TYPE, null, null, ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<AgentManagementErrorResponse> handleBadRequestException(BadRequestException ex) {
        log.error("BadRequestException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createAgentManagementErrorResponse(ex.getType(), ex.getTitle(), ex.getStatus(), ex.getDetail()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {ServiceException.class})
    public ResponseEntity<AgentManagementErrorResponse> handleServiceException(ServiceException ex) {
        log.error("ServiceException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createAgentManagementErrorResponse(ex.getType(), ex.getTitle(), ex.getStatus(), ex.getDetail()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<AgentManagementErrorResponse> handleNullPointerException(NullPointerException ex) {
        log.error("NullPointerException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createAgentManagementErrorResponse(ErrorConstants.DEFAULT_TYPE, Status.BAD_REQUEST.getReasonPhrase(), Status.BAD_REQUEST, "A null value causing failure of request"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<AgentManagementErrorResponse> handleNullPointerException(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createAgentManagementErrorResponse(ErrorConstants.DEFAULT_TYPE, Status.EXPECTATION_FAILED.getReasonPhrase(), Status.EXPECTATION_FAILED, "A Database Integrity Constraints is causing failure of request"), HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<AgentManagementErrorResponse> handleException(Exception ex) {
        log.error("Exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createAgentManagementErrorResponse(ErrorConstants.DEFAULT_TYPE, Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), Status.INTERNAL_SERVER_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private AgentManagementErrorResponse createAgentManagementErrorResponse(URI type, String title, StatusType statusCode, String detail) {
        return createAgentManagementErrorResponse(type, title, statusCode, detail, null);
    }

    private AgentManagementErrorResponse createAgentManagementErrorResponse(URI type, String title, StatusType statusCode, String detail, String errorCode) {
        return AgentManagementErrorResponse.builder().type(type.getPath()).title(title)
                .status(Optional.ofNullable(statusCode.getStatusCode()).orElse(0)).detail(detail).errorCode(errorCode).build();
    }
}
