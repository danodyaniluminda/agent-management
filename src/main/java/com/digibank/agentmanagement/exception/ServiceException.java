package com.digibank.agentmanagement.exception;

import com.digibank.agentmanagement.deserializer.responses.ApiResponse;
import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.utils.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import java.net.URI;

public class ServiceException extends AbstractThrowableProblem {

    private static final String TITLE = "Service Error";

    public ServiceException(ApiResponse apiResponse) {
        super(apiResponse.getType() != null ? URI.create(apiResponse.getType()) : null, apiResponse.getErrorCode(), Status.BAD_REQUEST, apiResponse.getDetail() );
    }



    public ServiceException(ApiError apiError) {
        super(ErrorConstants.DEFAULT_TYPE, TITLE, Status.BAD_REQUEST, apiError.getDescription());
    }

    public ServiceException(ApiError apiError, Throwable cause) {
        super(ErrorConstants.DEFAULT_TYPE, TITLE, Status.BAD_REQUEST, apiError.getDescription(), null, (ThrowableProblem) cause);
    }
}
