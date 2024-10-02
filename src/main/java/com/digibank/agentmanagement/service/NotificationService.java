package com.digibank.agentmanagement.service;


import com.digibank.agentmanagement.config.Constants;
import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.MFAChannel;
import com.digibank.agentmanagement.domain.UserType;
import com.digibank.agentmanagement.exception.DigibankRuntimeException;
import com.digibank.agentmanagement.repository.AgentDetailsRepository;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class NotificationService {

    private final String notificationServiceURL;
    private final AgentDetailsRepository agentDetailsRepository;
    private final String[] agentRegistrationNotificationList;

    public NotificationService(
            @Value("${services.notification.url}") String notificationServiceURL,
            AgentDetailsRepository agentDetailsRepository,
            @Value("${agent.registration.notification-list}")
                    String[] agentRegistrationNotificationList) {
        this.notificationServiceURL = notificationServiceURL;
        this.agentDetailsRepository = agentDetailsRepository;
        this.agentRegistrationNotificationList = agentRegistrationNotificationList;
    }

    public boolean validateMFACode(AgentDetails agentDetails  , String mfaCode){

        HttpResponse response =
                Unirest.post(notificationServiceURL + "api-internal/mfa/validate")
                        .header("Content-Type", "application/json")
                        .body(
                                ValidateMfaToken.builder()
                                        .userType(UserType.AGENT.toString())
                                        .phoneNumber(agentDetails.getPhoneNumberCountryCode() + agentDetails.getPhoneNo())
                                        .userId(agentDetails.getAgentId())
                                        .mfaCode(mfaCode)
                                        .build())
                        .asString();

        log.debug(response.getBody().toString());
        if (!response.isSuccess()) {
            log.info(response.getBody().toString());
            log.debug(response.getBody().toString());

            throw new DigibankRuntimeException("Error while Validating MFA Token");
        }
        return true;
    }


    public boolean validateMFACore(String mobileNo , String email  , Long agentId , String mfaCode , String UserType){

        HttpResponse response =
                Unirest.post(notificationServiceURL + "api-internal/mfa/validate")
                        .header("Content-Type", "application/json")
                        .body(
                                ValidateMfaToken.builder()
                                        //.userType(UserType.AGENT.toString())
                                        .userType(UserType)
                                        .phoneNumber(mobileNo)
                                        .userId(agentId)
                                        .mfaCode(mfaCode)
                                        .build())
                        .asString();

        log.debug(response.getBody().toString());
        if (!response.isSuccess()) {
            log.info(response.getBody().toString());
            log.debug(response.getBody().toString());

            throw new DigibankRuntimeException("Error while Validating MFA Token");
        }
        return true;
    }

    public void generateMFACodeAndSend(
            AgentDetails agentDetails  ,
            MFAChannel mfaChannel,
            Constants.EmailTemplate emailTemplate,
            Constants.SMSTemplate smsTemplate,
            Map<String, String> variables , boolean usingPushNotification , boolean usingEmail , boolean usingSMS) {
        log.info("Generating and sending PIN number for customer: {}", agentDetails);
        log.info("Notification Generation Request : {}" ,
                GenerateMFACodeRequest.builder()
                        .mfaChannel(mfaChannel)
                        .locale(agentDetails.getLocale().toString())
                        .email(agentDetails.getAgentEmailAddress())
                        .phoneNumber(agentDetails.getPhoneNumberCountryCode() + agentDetails.getPhoneNo())
                        .emailTemplateName(emailTemplate.toString())
                        .smsTemplateName(smsTemplate.toString())
                        .userType(UserType.AGENT.toString())
                        .userId(agentDetails.getAgentId())
                        .usingPushNotification(usingPushNotification)
                        .usingEmail(usingEmail)
                        .usingSMS(usingSMS)
                        .variables(variables)
                        .build().toString());



        HttpResponse response =
                Unirest.post(notificationServiceURL + "api-internal/mfa")
                        .header("Content-Type", "application/json")
                        .body(
                                GenerateMFACodeRequest.builder()
                                        .mfaChannel(mfaChannel)
                                        .locale(agentDetails.getLocale().toString())
                                        .email(agentDetails.getAgentEmailAddress())
                                        .phoneNumber(agentDetails.getPhoneNumberCountryCode() + agentDetails.getPhoneNo())
                                        .emailTemplateName(emailTemplate.toString())
                                        .smsTemplateName(smsTemplate.toString())
                                        .userType(UserType.AGENT.toString())
                                        .userId(agentDetails.getAgentId())
                                        .usingPushNotification(usingPushNotification)
                                        .usingEmail(usingEmail)
                                        .usingSMS(usingSMS)
                                        .variables(variables)
                                        .build())
                        .asString();
        if (!response.isSuccess()) {
            log.info("respone : {}",response.getBody().toString());


            throw new DigibankRuntimeException("Error while sending registration email and SMS");
        }
    }

    public void sendInformationNotification(AgentDetails agentDetails  ,
                                            MFAChannel mfaChannel,
                                            Map<String, String> variables , boolean usingPushNotification , boolean usingEmail , boolean usingSMS){
        log.info("Generating and sending Notifcation for customer: {}", agentDetails);
        log.info("Notification Generation Request : {}" ,
                GenerateMFACodeRequest.builder()
                        .mfaChannel(mfaChannel)
                        .locale(agentDetails.getLocale().toString())
                        .email(agentDetails.getAgentEmailAddress())
                        .phoneNumber(agentDetails.getPhoneNumberCountryCode() + agentDetails.getPhoneNo())
                        .emailTemplateName("INFORMATIVE_NOTIFICATION")
                        .smsTemplateName("INFORMATIVE_NOTIFICATION")
                        .userType(UserType.AGENT.toString())
                        .userId(agentDetails.getAgentId())
                        .usingPushNotification(usingPushNotification)
                        .usingEmail(usingEmail)
                        .usingSMS(usingSMS)
                        .variables(variables)
                        .build().toString());



        HttpResponse response =
                Unirest.post(notificationServiceURL + "api-internal/mfa")
                        .header("Content-Type", "application/json")
                        .body(
                                GenerateMFACodeRequest.builder()
                                        .mfaChannel(mfaChannel)
                                        .locale(agentDetails.getLocale().toString())
                                        .email(agentDetails.getAgentEmailAddress())
                                        .phoneNumber(agentDetails.getPhoneNumberCountryCode() + agentDetails.getPhoneNo())
                                        .emailTemplateName("INFORMATIVE_NOTIFICATION")
                                        .smsTemplateName("INFORMATIVE_NOTIFICATION")
                                        .userType(UserType.AGENT.toString())
                                        .userId(agentDetails.getAgentId())
                                        .usingPushNotification(usingPushNotification)
                                        .usingEmail(usingEmail)
                                        .usingSMS(usingSMS)
                                        .variables(variables)
                                        .build())
                        .asString();
        if (!response.isSuccess()) {
            log.info("respone : {}",response.getBody().toString());


            throw new DigibankRuntimeException("Error while sending registration email and SMS");
        }


    }

    public void sendSMSNotification(AgentDetails agentDetails  ,
                                            MFAChannel mfaChannel,String emailTemplate , String smsTemplate,String to,String bankCustomerId,
                                            Map<String, String> variables , boolean usingPushNotification , boolean usingEmail , boolean usingSMS){
        log.info("Generating and sending Notifcation for customer: {}", agentDetails);
        log.info("Notification Generation Request : {}" ,
                SendSMS.builder()
                        .mfarequest(false)
                        .locale(agentDetails.getLocale().toString())
                        .to(to)
                        .bankCustomerId(bankCustomerId)
                        .templateName(smsTemplate)
                        .variables(variables)
                        .build().toString());



        HttpResponse response =
                Unirest.post(notificationServiceURL + "api-internal/sms/send")
                        .header("Content-Type", "application/json")
                        .body(
//                                GenerateMFACodeRequest.builder()
//                                        .mfaChannel(mfaChannel)
//                                        .locale(agentDetails.getLocale().toString())
//                                        .email(agentDetails.getAgentEmailAddress())
//                                        .phoneNumber(agentDetails.getPhoneNumberCountryCode() + agentDetails.getPhoneNo())
//                                        .emailTemplateName(emailTemplate)
//                                        .smsTemplateName(smsTemplate)
//                                        .userType(UserType.AGENT.toString())
//                                        .userId(agentDetails.getAgentId())
//                                        .usingPushNotification(usingPushNotification)
//                                        .usingEmail(usingEmail)
//                                        .usingSMS(usingSMS)
//                                        .variables(variables)
//                                        .build())
        SendSMS.builder()
                .mfarequest(false)
                .locale(agentDetails.getLocale().toString())
                .to(to)
                .bankCustomerId(bankCustomerId)
                .templateName(smsTemplate)
                .variables(variables)
                .build())



                        .asString();
        if (!response.isSuccess()) {
            log.info("respone : {}",response.getBody().toString());


            throw new DigibankRuntimeException("Error while sending registration email and SMS");
        }


    }

    public void sendEmailNotification(AgentDetails agentDetails  ,
                                      MFAChannel mfaChannel,String emailTemplate , String smsTemplate,String to,String bankCustomerId,
                                      Map<String, String> variables , boolean usingPushNotification , boolean usingEmail , boolean usingSMS){
        log.info("Generating and sending Notifcation for customer: {}", agentDetails);
        log.info("Notification Generation Request : {}" ,
                SendEmail.builder()
                        .locale(agentDetails.getLocale().toString())
                        .to(to)
                        .templateName(smsTemplate)
                        .variables(variables)
                        .build().toString());



        HttpResponse response =
                Unirest.post(notificationServiceURL + "api-internal/email/send")
                        .header("Content-Type", "application/json")
                        .body(
//                                GenerateMFACodeRequest.builder()
//                                        .mfaChannel(mfaChannel)
//                                        .locale(agentDetails.getLocale().toString())
//                                        .email(agentDetails.getAgentEmailAddress())
//                                        .phoneNumber(agentDetails.getPhoneNumberCountryCode() + agentDetails.getPhoneNo())
//                                        .emailTemplateName(emailTemplate)
//                                        .smsTemplateName(smsTemplate)
//                                        .userType(UserType.AGENT.toString())
//                                        .userId(agentDetails.getAgentId())
//                                        .usingPushNotification(usingPushNotification)
//                                        .usingEmail(usingEmail)
//                                        .usingSMS(usingSMS)
//                                        .variables(variables)
//                                        .build())
                                SendEmail.builder()
                                        .locale(agentDetails.getLocale().toString())
                                        .to(to)
                                        .templateName(smsTemplate)
                                        .variables(variables)
                                        .build())



                        .asString();
        if (!response.isSuccess()) {
            log.info("respone : {}",response.getBody().toString());


            throw new DigibankRuntimeException("Error while sending registration email and SMS");
        }


    }



    public void generateMFACodeAndSendCore(
            String userType,
            String locale,
            String emailAddress ,
            String mobileNo,
            Long agentId,
            MFAChannel mfaChannel,
            Constants.EmailTemplate emailTemplate,
            Constants.SMSTemplate smsTemplate,
            Map<String, String> variables , boolean usingPushNotification , boolean usingEmail , boolean usingSMS) {
        log.info("Notification Generation Request : {}" ,
                GenerateMFACodeRequest.builder()
                        .mfaChannel(mfaChannel)
                        .locale(locale)
                        .email(emailAddress)
                        .phoneNumber(mobileNo)
                        .emailTemplateName(emailTemplate.toString())
                        .smsTemplateName(smsTemplate.toString())
                        .userType(userType)
                        .userId(agentId)
                        .usingPushNotification(usingPushNotification)
                        .usingEmail(usingEmail)
                        .usingSMS(usingSMS)
                        .variables(variables)
                        .build().toString());



        HttpResponse response =
                Unirest.post(notificationServiceURL + "api-internal/mfa")
                        .header("Content-Type", "application/json")
                        .body(
                                GenerateMFACodeRequest.builder()
                                        .mfaChannel(mfaChannel)
                                        .locale(locale)
                                        .email(emailAddress)
                                        .phoneNumber(mobileNo)
                                        .emailTemplateName(emailTemplate.toString())
                                        .smsTemplateName(smsTemplate.toString())
                                        .userType(userType)
                                        .userId(agentId)
                                        .usingPushNotification(usingPushNotification)
                                        .usingEmail(usingEmail)
                                        .usingSMS(usingSMS)
                                        .variables(variables)
                                        .build())
                        .asString();
        if (!response.isSuccess()) {
            log.info(response.getBody().toString());
            log.debug(response.getBody().toString());

            throw new DigibankRuntimeException("Error while sending registration email and SMS");
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class GenerateMFACodeRequest {
        private MFAChannel mfaChannel;
        private String email;
        private String phoneNumber;
        private String smsTemplateName;
        private String emailTemplateName;
        private boolean usingPushNotification;
        private boolean usingSMS;
        private boolean usingEmail;
        private String locale;
        private String userType;
        private Long userId;
        private Map<String, String> variables;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class ValidateMfaToken {
        private String userType;
        private Long userId;
        private String mfaCode;
        private String phoneNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class SendSMS {
        private String templateName;
        private String locale;
        private String bankCustomerId;
        private String to;
        private Map<String, String> variables;
        private Boolean mfarequest;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class SendEmail {
        private String templateName;
        private String locale;
        private String to;
        private Map<String, String> variables;

    }
}
