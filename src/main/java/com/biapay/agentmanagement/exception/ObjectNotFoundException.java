package com.biapay.agentmanagement.exception;

import com.biapay.agentmanagement.utils.ApiError;
import com.biapay.agentmanagement.utils.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

public class ObjectNotFoundException extends AbstractThrowableProblem {

    public ObjectNotFoundException(ApiError apiError) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.NOT_FOUND, apiError.getDescription());
    }

    public ObjectNotFoundException(ApiError apiError, String value) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.NOT_FOUND, apiError.getDescription().replace("#value", value));
    }

    public ObjectNotFoundException(ApiError apiError, Throwable cause) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.NOT_FOUND, apiError.getDescription(), null, (ThrowableProblem) cause);
    }
}
