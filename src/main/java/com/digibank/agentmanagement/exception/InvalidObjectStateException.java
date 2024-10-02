package com.digibank.agentmanagement.exception;

import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.utils.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class InvalidObjectStateException extends AbstractThrowableProblem {

    public InvalidObjectStateException(ApiError apiError) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.BAD_REQUEST, apiError.getDescription());
    }
}
