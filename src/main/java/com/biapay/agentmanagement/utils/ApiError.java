package com.biapay.agentmanagement.utils;

public enum ApiError {
    INVALID_AGENT("Invalid Agent"),
    INVALID_AGENT_STATUS("Invalid AGENT status"),
    EMAIL_ADDRESS_IS_REQUIRED("Email address is required"),
    PHONE_NUMBER_IS_REQUIRED("Phone number is required"),
    AGENT_DOES_NOT_HAVE_EMAIL_ADDRESS_REGISTERED("AGENT doesn't have email address"),
    MFA_VERIFICATION_FAILED("MFA verification failed"),
    AGENT_ALREADY_EXISTS_WITH_THIS_PHONE_NUMBER("AGENT already exist with this phone number"),
    AGENT_ALREADY_EXISTS_WITH_THIS_EMAIL("AGENT already exist with this email"),
    EXISTING_BANK_CUSTOMER_REGISTRATION_DOES_NOT_REQUIRE_FIRST_NAME_AND_LAST_NAME(
            "Existing AGENT registration doesn't require first name and last name"),
    NON_EXISTING_BANK_CUSTOMER_REGISTRATION_REQUIRE_FIRST_NAME_AND_LAST_NAME(
            "Non existing AGENT registration require first name and last name"),
    NON_EXISTING_BANK_CUSTOMER_REGISTRATION_DOES_NOT_REQUIRE_BANK_CUSTOMER_ID(
            "Non existing AGENT registration doesn't requires bank Customer id"),
    EXISTING_BANK_CUSTOMER_REGISTRATION_REQUIRES_BANK_CUSTOMER_ID(
            "Existing AGENT registration requires bank AGENT id"),
    PASSWORD_AND_CONFIRM_PASSWORD_DOES_NOT_MATCH("Password and confirm password doesn't match"),
    DATE_OF_BIRTH_IS_REQUIRED("Date of birth is required"),
    ADDRESS_IS_REQUIRED("Address is required"),
    CITY_OF_RESIDENCE_IS_REQUIRED("City of residence is required"),
    ID_DOCUMENT_IS_REQUIRED("ID Document file is required"),
    CAN_NOT_FIND_AGENT_IN_CORE_BANKING(
            "Can't find AGENT in bank database with the details provided"),
    INVALID_CURRENT_PASSWORD("Invalid current password"),
    INVALID_DOCUMENT("Invalid document"),
    INVALID_REQUEST_MONEY_IDENTIFIER("Invalid request money identifier"),
    INVALID_MFA_TOKEN("Invalid MFA token"),
    INVALID_BENEFICIARY_IDENTIFIER("Invalid beneficiary id"),
    BENEFICIARY_NOT_FOUND("Beneficiary not found"),
    COULD_NOT_DETERMINE_USER_TYPE_FROM_TOKEN("Couldn't determine the user type from logged in user"),
    EMAIL_TEMPLATE_IS_REQUIRED("Email template is required"),
    SMS_TEMPLATE_IS_REQUIRED("SMS template is required"),
    AT_LEAST_ONE_NOTIFICATION_METHOD_IS_REQUIRED("At least one notification method is required"),
    EXISTING_BANK_AGENT_CANNOT_SWITCH_AS_BANK_AGENT(
            "You are already an existing bank AGENT"),
    AGENT_TYPE_IS_REQUIRED("Agent Type is required"),
    MFA_TOKEN_IS_REQUIRED("MFA token is required"),
    INVALID_AUTH_TOKEN("Invalid Authentication Token"),
    SERVICE_ERROR("Service Error"),
    OBJECT_NOT_FOUND("No Data Found with provided value: #value"),
    INVALID_INPUT("Input object or parameter must not be null"),
    INVALID_INPUT_PARAMETER("Input parameter must not be null"),
    INVALID_INPUT_OBJECT("Input object must not be null"),
	REQUEST_FORMAT_ERROR("Invalid Request"),
    INVALID_ACCOUNT_NUMBER("Invalid Bank Account Number"),
    SUPER_AGENT_PHONE_REQUIRED("Super Agent Details Required[Missing Param:superAgentPhone]"),
    INVALID_SUPER_AGENT_DETAILS("Invalid Super Agent Details"),
    INVALID_SUPER_AGENT_STATUS("Super Agent Status is Not Active"),
    AGENT_NOT_FOUND("Agent with mobile Number is not found"),
    AGENT_NOT_ACTIVE("Account is not Active, please activate account and try again"),
    OBJECT_ALREADY_EXIST("Objet already Exist for parameter having value = :value"),
    DEFAULT_PACKAGE_ERROR("Default Package Not Available for Requested AgentType"),
    BUSSINESS_REGISTRATION_DATE_MISSING("Business Registration Date Is Missing "),
    TRADE_REGISTRATION_NUMBER_MISSING("Trade Registration Number Is Missing "),
    TAX_PAYER_NUMBER_MISSING("Tax Payer Number Is Missing "),
    DAILY_TRANSACTION_COUNT_LIMIT_REACHED("Daily Transaction Count Limit Reached"),
    WEEKLY_TRANSACTION_COUNT_LIMIT_REACHED("Weekly Transaction Count Limit Reached"),
    MONTHLY_TRANSACTION_COUNT_LIMIT_REACHED("Monthly Transaction Count Limit Reached"),
    DAILY_TRANSACTION_AMOUNT_LIMIT_REACHED("Daily Transaction Amount Limit Reached"),
    WEEKLY_TRANSACTION_AMOUNT_LIMIT_REACHED("Weekly Transaction Amount Limit Reached"),
    MONTHLY_TRANSACTION_AMOUNT_LIMIT_REACHED("Monthly Transaction Amount Limit Reached"),
    DAILY_TRANSACTION_AMOUNT_ABOVE_LIMIT("This amount crossed Daily Transaction Amount Limit. You have remaining Amount = :amount"),
    WEEKLY_TRANSACTION_AMOUNT_ABOVE_LIMIT("This amount crossed Weekly Transaction Amount Limit. You have remaining Amount = :amount"),
    MONTHLY_TRANSACTION_AMOUNT_ABOVE_LIMIT("This amount crossed Monthly Transaction Amount Limit. You have remaining Amount = :amount"),
    PACKAGE_CURRENCY_NOT_AVAILABLE("Currency information is not available for this agent package"),
    PACKAGE_OPERATION_NOT_AVAILABLE("Operation information is not available for this agent package"),
    PACKAGE_OPERATION_NOT_ACTIVE("Operation permission is not active for this agent package"),
    OPERATION_NOT_ALLOWED("You don't have permission for Operation name = :value"),
    PACKAGE_NOT_ACTIVE("Your package is not active"),
    CURRENCY_NOT_AVAILABLE("Currency Not Available in System"),
    PERMISSION_DENIED("Permission Denied"),
    FILE_PROCESSING_ERROR("Error while processing file data: "),
    INVALID_CUSTOMER("Invalid Customer Details")
    ;

    private String description;

    ApiError(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
