package com.biapay.agentmanagement.exception;

import com.biapay.agentmanagement.deserializer.responses.ApiResponse;
import com.biapay.agentmanagement.utils.ApiError;
import com.biapay.agentmanagement.utils.ErrorConstants;
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
