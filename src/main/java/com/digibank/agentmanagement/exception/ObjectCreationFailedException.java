package com.digibank.agentmanagement.exception;

import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.utils.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ObjectCreationFailedException extends AbstractThrowableProblem {

    public ObjectCreationFailedException(ApiError apiError, String value) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.BAD_REQUEST, apiError.getDescription().replace(":value", value));
    }
}
