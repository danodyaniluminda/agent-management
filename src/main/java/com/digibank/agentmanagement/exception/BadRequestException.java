package com.digibank.agentmanagement.exception;

import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.utils.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import javax.annotation.concurrent.Immutable;
import java.net.URI;

import org.zalando.problem.Status;

import java.util.Map;

public class BadRequestException extends AbstractThrowableProblem {

    private static final String DEFAULT_TITLE = "Invalid Request";

    private String errorCode;

    public BadRequestException(ApiError apiError) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.BAD_REQUEST, apiError.getDescription());
    }

    public BadRequestException(String errorCode, String errorMessage) {
        this(ErrorConstants.DEFAULT_TYPE, DEFAULT_TITLE, errorMessage);
    }

    public BadRequestException(ApiError apiError, String errorCode) {
        super(ErrorConstants.DEFAULT_TYPE, errorCode, Status.BAD_REQUEST, apiError.getDescription());
    }

    public BadRequestException(ApiError apiError, Status status, String errorCode) {
        super(ErrorConstants.DEFAULT_TYPE, errorCode, status, apiError.getDescription());
    }

    public BadRequestException(String errorMessage) {
        this(ErrorConstants.DEFAULT_TYPE, DEFAULT_TITLE, errorMessage);
    }

    public BadRequestException(URI type, String title, String errorMessage) {
        super(type, title, Status.BAD_REQUEST, errorMessage, null, null);
    }

    public BadRequestException(String detail, Status status, String errorCode) {
        super(ErrorConstants.DEFAULT_TYPE, errorCode, status, detail);
    }
}
