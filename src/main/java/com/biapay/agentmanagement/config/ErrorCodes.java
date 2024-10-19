package com.biapay.agentmanagement.config;

public class ErrorCodes {

    // Ok
    public static final String PROCESSED_OK_DESCRIPTION = "PROCESSED OK";
    public static final String PROCESSED_OK_CODE = "000";

    // Invalid Transaction
    public static final String INVALID_TRANSACTION_ERROR_DESCRIPTION = "INVALID TRANSACTION REQUEST";
    public static final String INVALID_TRANSACTION_ERROR_CODE = "001";


    // Message Format Error
    public static final String MESSAGE_FORMAT_ERROR_DESCRIPTION = "MESSAGE FORMAT ERROR";
    public static final String MESSAGE_FORMAT_ERROR_CODE = "003";

    // Permission Error
    public static final String TRANSACTION_PERMISSION_ERROR_DESCRIPTION = "TRANSACTION NOT PERMITTED";
    public static final String TRANSACTION_PERMISSION_ERROR_CODE = "004";

    // Invalid Account Number
    public static final String INVALID_ACCOUNT_ERROR_DESCRIPTION = "INVALID ACCOUNT NUMBER";
    public static final String INVALID_ACCOUNT_ERROR_CODE = "005";

    // Invalid Customer ID
    public static final String INVALID_CUSTOMER_ERROR_DESCRIPTION = "INVALID CUSTOMER ID";
    public static final String INVALID_CUSTOMER_ERROR_CODE = "006";

    // Auth Token Error
    public static final String INVALID_AUTH_TOKEN_DESCRIPTION = "INVALID AUTH TOKEN";
    public static final String INVALID_AUTH_TOKEN_CODE = "007";

    // Daily Limit Exceeded
    public static final String DAILY_LIMIT_EXCEEDED_DESCRIPTION = "DAILY LIMIT EXCEEDED";
    public static final String DAILY_LIMIT_EXCEEDED_CODE = "301";

    // Weekly Limit Exceeded
    public static final String WEEKLY_LIMIT_EXCEEDED_DESCRIPTION = "WEEKLY LIMIT EXCEEDED";
    public static final String WEEKLY_LIMIT_EXCEEDED_CODE = "302";

    // Monthly Limit Exceeded
    public static final String MONTHLY_LIMIT_EXCEEDED_DESCRIPTION = "MONTHLY LIMIT EXCEEDED";
    public static final String MONTHLY_LIMIT_EXCEEDED_CODE = "303";

    // Invalid OTP
    public static final String INVALID_OTP_DESCRIPTION = "INVALID OTP";
    public static final String INVALID_OTP_CODE = "501";

    // Invalid Agent Details
    public static final String INVALID_AGENT_DETAILS_DESCRIPTION = "INVALID AGENT DETAILS";
    public static final String INVALID_AGENT_DETAILS_CODE = "601";

    // Invalid Request
    public static final String INVALID_REQUEST = "INVALID REQUEST";
    public static final String INVALID_REQUEST_CODE = "400";

}
