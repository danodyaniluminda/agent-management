package com.digibank.agentmanagement.exception;

import com.digibank.agentmanagement.utils.ApiError;
import com.digibank.agentmanagement.utils.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class InvalidInputException extends AbstractThrowableProblem {

    public InvalidInputException(ApiError apiError) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.NOT_FOUND, apiError.getDescription());
    }
}
