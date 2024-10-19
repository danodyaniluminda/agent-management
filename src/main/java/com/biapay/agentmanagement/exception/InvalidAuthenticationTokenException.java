package com.biapay.agentmanagement.exception;

import com.biapay.agentmanagement.utils.ApiError;
import com.biapay.agentmanagement.utils.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;


public class InvalidAuthenticationTokenException extends AbstractThrowableProblem {

    public InvalidAuthenticationTokenException(ApiError apiError) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.BAD_REQUEST, apiError.getDescription());
    }

    public InvalidAuthenticationTokenException(ApiError apiError, Throwable cause) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.BAD_REQUEST, apiError.getDescription(), null, (ThrowableProblem) cause);
    }

}
