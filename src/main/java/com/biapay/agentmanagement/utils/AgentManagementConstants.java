package com.biapay.agentmanagement.utils;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.format.DateTimeFormatter;

@Getter
public class AgentManagementConstants {

    public static final String REGISTRATION_TYPE = "registrationType";
    public static final String BANK_CUSTOMER_ID = "bankCustomerId";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PHONE_NUMBER_COUNTRY_CODE = "phoneNumberCountryCode";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String ID_DOCUMENT_TYPE = "idDocumentType";
    public static final String ID_DOCUMENT_FILE = "idDocumentFile";
    public static final String ID_DOCUMENT_NUMBER = "idDocumentNumber";
    public static final String ID_DOCUMENT_EXPIRY_DATE = "idDocumentExpiryDate";
    public static final String EMAIL_ADDRESS = "emailAddress";
    public static final String UIN = "uin";
    public static final String TC_ACCEPTED = "tcAccepted";
    public static final String LOCALE = "locale";
    public static final String CURRENCY_CODE = "currencyCode";
    public static final String AGENT_BANKER_PHONE_NUMBER = "agentBankerPhoneNumber";
    public static final String SELFIE_DOCUMENT_FILE = "selfieDocumentFile";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String ADDRESS = "address";
    public static final String CITY_OF_RESIDENCE = "cityOfResidence";

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TOKEN_TYPE = "Bearer";

    public static final Integer DEFAULT_PAGE_SIZE = 10;

    public static final DateTimeFormatter YEAR_MONTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String JAVA_TMP_PATH = StringUtils.appendIfMissing(System.getProperty("java.io.tmpdir"), File.separator);
}
