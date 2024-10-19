package com.biapay.agentmanagement.config;

public class Constants {
    public static String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static String SPRING_PROFILE_TEST = "test";
    public static String SPRING_PROFILE_PRODUCTION = "prod";

    public static final String SYSTEM = "System";


    public static final String MOBILE_COUNTRY_CODE = "mobileCountryCode";
    public static final String USER_ID = "USER_ID";
    public static final String USER_TYPE = "USER_TYPE";

    public static final String KEYCLOAK_AGENT_PREFIX = "a_";


    public enum Roles {
        ROLE_AGENT ,
        ROLE_CUSTOMER,
        ROLE_ADMIN
    }

    public enum EmailTemplate {
        AGENT_REGISTRATION_PIN,
        CUSTOMER_REGISTRATION_PIN,
        FORGOT_PASSWORD,
        TRANSACTION_MFA_REQUEST,
        USER_REGISTRATION_NOTIFICATION
    }

    public enum SMSTemplate {
        AGENT_REGISTRATION_PIN,
        CUSTOMER_REGISTRATION_PIN,
        FORGOT_PASSWORD,
        TRANSACTION_MFA_REQUEST,
    }

}
