package com.biapay.agentmanagement.exception;

import com.biapay.agentmanagement.utils.ApiError;
import com.biapay.agentmanagement.utils.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class OperationPermissionException extends AbstractThrowableProblem {

    public OperationPermissionException(ApiError apiError) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.PRECONDITION_FAILED, apiError.getDescription());
    }
}
