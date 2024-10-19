package com.biapay.agentmanagement.exception;

import com.biapay.agentmanagement.utils.ApiError;
import com.biapay.agentmanagement.utils.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class InvalidInputException extends AbstractThrowableProblem {

    public InvalidInputException(ApiError apiError) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.NOT_FOUND, apiError.getDescription());
    }
}
