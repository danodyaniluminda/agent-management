package com.biapay.agentmanagement.utils;
import java.net.URI;


public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "digibank";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE =
            URI.create(PROBLEM_BASE_URL + "/constraint-violation");

    private ErrorConstants() {}
}
