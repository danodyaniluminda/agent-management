package com.biapay.agentmanagement.exception;

public class BIAPayRuntimeException extends RuntimeException {
    public BIAPayRuntimeException() {}

    public BIAPayRuntimeException(String message) {
        super(message);
    }

    public BIAPayRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BIAPayRuntimeException(Throwable cause) {
        super(cause);
    }

    public BIAPayRuntimeException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
