package com.digibank.agentmanagement.exception;

public class DigibankRuntimeException extends RuntimeException {
    public DigibankRuntimeException() {}

    public DigibankRuntimeException(String message) {
        super(message);
    }

    public DigibankRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DigibankRuntimeException(Throwable cause) {
        super(cause);
    }

    public DigibankRuntimeException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
