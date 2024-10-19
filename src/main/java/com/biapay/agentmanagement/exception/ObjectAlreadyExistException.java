package com.biapay.agentmanagement.exception;

import com.biapay.agentmanagement.utils.ApiError;
import com.biapay.agentmanagement.utils.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ObjectAlreadyExistException extends AbstractThrowableProblem {

    public ObjectAlreadyExistException(ApiError apiError, String value) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.BAD_REQUEST, apiError.getDescription().replace(":value", value));
    }
}
