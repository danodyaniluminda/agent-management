package com.biapay.agentmanagement.exception;

import com.biapay.agentmanagement.utils.ApiError;
import com.biapay.agentmanagement.utils.ErrorConstants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class TransactionLimitException extends AbstractThrowableProblem {

    public TransactionLimitException(ApiError apiError) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.PRECONDITION_FAILED, apiError.getDescription());
    }

    public TransactionLimitException(ApiError apiError, String amount) {
        super(ErrorConstants.DEFAULT_TYPE, apiError.name(), Status.PRECONDITION_FAILED,
                apiError.getDescription().replace(":amount", amount));
    }
}
