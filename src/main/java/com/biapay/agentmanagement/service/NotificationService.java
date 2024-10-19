package com.biapay.agentmanagement.service;

import com.biapay.agentmanagement.config.AppConfig;
import com.biapay.agentmanagement.domain.AgentDetails;
import com.biapay.agentmanagement.domain.AgentType;
import com.biapay.agentmanagement.exception.BIAPayRuntimeException;
import com.biapay.core.constant.BIAConstants;
import com.biapay.core.constant.BIAConstants.Email;
import com.biapay.core.constant.MessageType;
import com.biapay.core.dto.NotificationMessageDTO;
import com.biapay.core.exception.NotFoundException;
import com.biapay.core.model.KYC_Form;
import com.biapay.core.model.Language;
import com.biapay.core.model.MFACode;
import com.biapay.core.model.Role;
import com.biapay.core.model.SMSTemplate;
import com.biapay.core.model.User;
import com.biapay.core.model.UserType;
import com.biapay.core.model.user.UserKYC;
import com.biapay.core.repository.EmailTemplateRepository;
import com.biapay.core.repository.LanguageRepository;
import com.biapay.core.repository.PayLinkRepository;
import com.biapay.core.repository.RoleRepository;
import com.biapay.core.repository.SMSTemplateRepository;
import com.biapay.core.repository.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

  @Value("${backoffice.domain}")
  private String backOfficeDomain;

  @Value("${backoffice.adminLogin}")
  private String adminLogin;

  @Autowired
  private EmailTemplateRepository emailTemplateRepository;
  @Autowired
  private LanguageRepository languageRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private AppConfig appConfig;
  @Autowired
  private SMSTemplateRepository smsTemplateRepository;
  @Autowired
  private KafkaProducer kafkaProducer;

  public void agentRegistrationEmailConfirmation(AgentDetails agentDetails) {
    Language language;
    if (agentDetails.getRootUser().getLanguage() == null) {
      language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    } else {
      language = agentDetails.getRootUser().getLanguage();
    }

    Map<String, Object> map = new HashMap<>();
    map.put("name", agentDetails.getRootUser().getName());
    map.put("agentName", agentDetails.getFirstName() + " " + agentDetails.getLastName());
    map.put("emailVerificationToken", agentDetails.getRootUser().getEmailVerificationToken());
    map.put("domain", backOfficeDomain);

    NotificationMessageDTO messageDTO =
        NotificationMessageDTO.builder()
            .destination(agentDetails.getRootUser().getEmail())
            .type(MessageType.EMAIL_NOTIFICATION)
            .variables(map)
            .template(agentDetails.getAgentType() == AgentType.AGENT
                ? Email.AGENT_REGISTRATION_EMAIL_CONFIRMATION
                : Email.AGENT_MEMBER_REGISTRATION_EMAIL_CONFIRMATION)
            .locale(language.getName())
            .build();

    log.info("Sending instruction to kafka for streaming notification");
    kafkaProducer.sendNotification(messageDTO);

    //    sendEmailService.sendEmailUsingMustache(
    //        emailTemplate, merchant.getRootUser().getEmail(), map, null);
  }

  public void forgotPasswordEmail(User user) {
    //    EmailTemplate emailTemplate = null;
    //    if (user.getLanguage() == null) {
    //      Language language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.FORGOT_PASSWORD_URL_EMAIL, language.getId());
    //    } else {
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.FORGOT_PASSWORD_URL_EMAIL, user.getLanguage().getId());
    //    }
    Language language;
    if (user.getLanguage() == null) {
      language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    } else {
      language = user.getLanguage();
    }

    Map<String, Object> map = new HashMap<>();
    map.put("name", user.getName());
    map.put("forgotPasswordToken", user.getResetPasswordToken());
    map.put("domain", backOfficeDomain);

    NotificationMessageDTO messageDTO =
        NotificationMessageDTO.builder()
            .destination(user.getEmail())
            .type(MessageType.EMAIL_NOTIFICATION)
            .variables(map)
            .template(Email.FORGOT_PASSWORD_URL_EMAIL)
            .locale(language.getName())
            .build();

    kafkaProducer.sendNotification(messageDTO);

    //    sendEmailService.sendEmailUsingMustache(emailTemplate, user.getEmail(), map, null);
  }

  public void resetPasswordConfirmationEmail(User user) {
    //    EmailTemplate emailTemplate = null;
    //    if (user.getLanguage() == null) {
    //      Language language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.RESET_PASSWORD_EMAIL_CONFIRMATION, language.getId());
    //    } else {
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.RESET_PASSWORD_EMAIL_CONFIRMATION, user.getLanguage().getId());
    //    }

    Language language;
    if (user.getLanguage() == null) {
      language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    } else {
      language = user.getLanguage();
    }

    Map<String, Object> map = new HashMap<>();
    map.put("name", user.getName());

    NotificationMessageDTO messageDTO =
        NotificationMessageDTO.builder()
            .destination(user.getEmail())
            .type(MessageType.EMAIL_NOTIFICATION)
            .variables(map)
            .template(Email.RESET_PASSWORD_EMAIL_CONFIRMATION)
            .locale(language.getName())
            .build();

    kafkaProducer.sendNotification(messageDTO);

    //    sendEmailService.sendEmailUsingMustache(emailTemplate, user.getEmail(), map, null);
  }

  public void kycSubmittedEmail(KYC_Form kycForm) {
    Role role = roleRepository.findByName(BIAConstants.Role.ROLE_MAKER);
    List<User> adminUserList = userRepository.findUserByRole(role);
    adminUserList.stream()
        .forEach(
            adminUser -> {
              //              EmailTemplate emailTemplate = null;
              User user =
                  userRepository
                      .findUserByEmailAndUserType(adminUser.getEmail(), UserType.ADMIN)
                      .get();
              //              if (user.getLanguage() == null) {
              //                Language language =
              // languageRepository.findByName(BIAConstants.Language.ENGLISH);
              //                emailTemplate =
              //                    emailTemplateRepository.findByTemplateNameAndLanguage(
              //                        Email.KYC_SUBMITTED_EMAIL, language.getId());
              //              } else {
              //                emailTemplate =
              //                    emailTemplateRepository.findByTemplateNameAndLanguage(
              //                        Email.KYC_SUBMITTED_EMAIL, user.getLanguage().getId());
              //              }

              Language language;
              if (user.getLanguage() == null) {
                language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
              } else {
                language = user.getLanguage();
              }

              Map<String, Object> map = new HashMap<>();
              map.put("name", user.getName());
              map.put("merchant", kycForm.getMerchant().getMerchantName());
              map.put("merchantEmail", kycForm.getMerchant().getRootUser().getEmail());

              NotificationMessageDTO messageDTO =
                  NotificationMessageDTO.builder()
                      .destination(user.getEmail())
                      .type(MessageType.EMAIL_NOTIFICATION)
                      .variables(map)
                      .template(Email.KYC_SUBMITTED_EMAIL)
                      .locale(language.getName())
                      .build();

              kafkaProducer.sendNotification(messageDTO);

              //              sendEmailService.sendEmailUsingMustache(emailTemplate,
              // user.getEmail(), map, null);
            });
  }

  public void kycPendingApprovalEmail(KYC_Form kycForm) {
    Role role = roleRepository.findByName(BIAConstants.Role.ROLE_CHECKER);
    List<User> adminUserList = userRepository.findUserByRole(role);
    adminUserList.forEach(
        adminUser -> {
          //              EmailTemplate emailTemplate = null;
          User user =
              userRepository.findUserByEmailAndUserType(adminUser.getEmail(), UserType.ADMIN).get();
          //              if (user.getLanguage() == null) {
          //                Language language =
          // languageRepository.findByName(BIAConstants.Language.ENGLISH);
          //                emailTemplate =
          //                    emailTemplateRepository.findByTemplateNameAndLanguage(
          //                        Email.KYC_PENDING_APPROVAL, language.getId());
          //              } else {
          //                emailTemplate =
          //                    emailTemplateRepository.findByTemplateNameAndLanguage(
          //                        Email.KYC_PENDING_APPROVAL, user.getLanguage().getId());
          //              }
          Language language;
          if (user.getLanguage() == null) {
            language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
          } else {
            language = user.getLanguage();
          }

          Map<String, Object> map = new HashMap<>();
          map.put("name", user.getName());
          map.put("merchant", kycForm.getMerchant().getMerchantName());
          map.put("merchantEmail", kycForm.getMerchant().getRootUser().getEmail());

          NotificationMessageDTO messageDTO =
              NotificationMessageDTO.builder()
                  .destination(user.getEmail())
                  .type(MessageType.EMAIL_NOTIFICATION)
                  .variables(map)
                  .template(Email.KYC_PENDING_APPROVAL)
                  .locale(language.getName())
                  .build();

          kafkaProducer.sendNotification(messageDTO);

          //              sendEmailService.sendEmailUsingMustache(emailTemplate,
          // user.getEmail(), map, null);
        });
  }

  public void merchantKycApprovedEmail(KYC_Form kycForm) {
    //    EmailTemplate emailTemplate = null;
    User user = kycForm.getMerchant().getRootUser();
    //    if (user.getLanguage() == null) {
    //      Language language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.KYC_APPROVED_EMAIL, language.getId());
    //    } else {
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.KYC_APPROVED_EMAIL, user.getLanguage().getId());
    //    }
    Language language;
    if (user.getLanguage() == null) {
      language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    } else {
      language = user.getLanguage();
    }

    Map<String, Object> map = new HashMap<>();
    map.put("name", user.getName());
    map.put("merchant", kycForm.getMerchant().getMerchantName());
    map.put("merchantEmail", kycForm.getMerchant().getRootUser().getEmail());

    NotificationMessageDTO messageDTO =
        NotificationMessageDTO.builder()
            .destination(user.getEmail())
            .type(MessageType.EMAIL_NOTIFICATION)
            .variables(map)
            .template(Email.KYC_APPROVED_EMAIL)
            .locale(language.getName())
            .build();

    kafkaProducer.sendNotification(messageDTO);

    //    sendEmailService.sendEmailUsingMustache(emailTemplate, user.getEmail(), map, null);
  }

  public void customerKycApprovedEmail(UserKYC userKYC) {
    //    EmailTemplate emailTemplate = null;
    User user = userKYC.getCustomerData().getUser();
    //    if (user.getLanguage() == null) {
    //      Language language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.KYC_APPROVED_EMAIL, language.getId());
    //    } else {
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.KYC_APPROVED_EMAIL, user.getLanguage().getId());
    //    }
    Language language;
    if (user.getLanguage() == null) {
      language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    } else {
      language = user.getLanguage();
    }

    Map<String, Object> map = new HashMap<>();
    map.put("name", user.getName());
    map.put("Customer", userKYC.getCustomerData().getUserName());
    map.put("Email", user.getEmail());

    NotificationMessageDTO messageDTO =
        NotificationMessageDTO.builder()
            .destination(user.getEmail())
            .type(MessageType.EMAIL_NOTIFICATION)
            .variables(map)
            .template(Email.KYC_APPROVED_EMAIL)
            .locale(language.getName())
            .build();

    kafkaProducer.sendNotification(messageDTO);

    //    sendEmailService.sendEmailUsingMustache(emailTemplate, user.getEmail(), map, null);
  }

  public void kycRejectedEmail(KYC_Form kycForm, List<String> rejectReasons) {
    //    EmailTemplate emailTemplate = null;
    User user = kycForm.getMerchant().getRootUser();
    //    if (user.getLanguage() == null) {
    //      Language language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.KYC_REJECTED_EMAIL, language.getId());
    //    } else {
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.KYC_REJECTED_EMAIL, user.getLanguage().getId());
    //    }
    Language language;
    if (user.getLanguage() == null) {
      language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    } else {
      language = user.getLanguage();
    }

    Map<String, Object> map = new HashMap<>();
    map.put("name", user.getName());
    map.put("merchant", kycForm.getMerchant().getMerchantName());
    map.put("merchantEmail", kycForm.getMerchant().getRootUser().getEmail());
    map.put("rejectionReasons", rejectReasons);

    NotificationMessageDTO messageDTO =
        NotificationMessageDTO.builder()
            .destination(user.getEmail())
            .type(MessageType.EMAIL_NOTIFICATION)
            .variables(map)
            .template(Email.KYC_REJECTED_EMAIL)
            .locale(language.getName())
            .build();

    kafkaProducer.sendNotification(messageDTO);

    //    sendEmailService.sendEmailUsingMustache(emailTemplate, user.getEmail(), map, null);
  }

  /**
   * @param user
   */
  public void sendKYCReminderEmail(User user) {
    //    EmailTemplate emailTemplate = null;
    //    if (user.getLanguage() == null) {
    //      Language language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.KYC_NOT_SENT_ALERT_EMAIL, language.getId());
    //    } else {
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.KYC_NOT_SENT_ALERT_EMAIL, user.getLanguage().getId());
    //    }
    Language language;
    if (user.getLanguage() == null) {
      language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    } else {
      language = user.getLanguage();
    }
    if (language != null) {
      Map<String, Object> map = new HashMap<>();
      map.put("name", user.getName());
      map.put("domain", backOfficeDomain);

      NotificationMessageDTO messageDTO =
          NotificationMessageDTO.builder()
              .destination(user.getEmail())
              .type(MessageType.EMAIL_NOTIFICATION)
              .variables(map)
              .template(Email.KYC_NOT_SENT_ALERT_EMAIL)
              .locale(language.getName())
              .build();

      kafkaProducer.sendNotification(messageDTO);

      //      sendEmailService.sendEmailUsingMustache(emailTemplate, user.getEmail(), map, null);
    }
  }

  /**
   * @param user
   */
  public void sendAccountSuspendedBecauseOfKYCEmail(User user) {
    //    EmailTemplate emailTemplate = null;
    //    if (user.getLanguage() == null) {
    //      Language language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.ACCOUNT_SUSPENDED_BECAUSE_OF_KYC_MISSING_EMAIL, language.getId());
    //    } else {
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.ACCOUNT_SUSPENDED_BECAUSE_OF_KYC_MISSING_EMAIL,
    // user.getLanguage().getId());
    //    }
    Language language;
    if (user.getLanguage() == null) {
      language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    } else {
      language = user.getLanguage();
    }

    if (language != null) {
      Map<String, Object> map = new HashMap<>();
      map.put("name", user.getName());
      map.put("domain", backOfficeDomain);

      NotificationMessageDTO messageDTO =
          NotificationMessageDTO.builder()
              .destination(user.getEmail())
              .type(MessageType.EMAIL_NOTIFICATION)
              .variables(map)
              .template(Email.ACCOUNT_SUSPENDED_BECAUSE_OF_KYC_MISSING_EMAIL)
              .locale(language.getName())
              .build();

      kafkaProducer.sendNotification(messageDTO);

      //      sendEmailService.sendEmailUsingMustache(emailTemplate, user.getEmail(), map, null);
    }
  }

  /**
   * @param user
   */
  public void sendAccountInactivatedBecauseOfKYCEmail(User user) {
    //    EmailTemplate emailTemplate = null;
    //    if (user.getLanguage() == null) {
    //      Language language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.ACCOUNT_INACTIVATED_BECAUSE_OF_KYC_MISSING_EMAIL, language.getId());
    //    } else {
    //      emailTemplate =
    //          emailTemplateRepository.findByTemplateNameAndLanguage(
    //              Email.ACCOUNT_SUSPENDED_BECAUSE_OF_KYC_MISSING_EMAIL,
    // user.getLanguage().getId());
    //    }
    Language language;
    if (user.getLanguage() == null) {
      language = languageRepository.findByName(BIAConstants.Language.ENGLISH);
    } else {
      language = user.getLanguage();
    }

    if (language != null) {
      Map<String, Object> map = new HashMap<>();
      map.put("name", user.getName());
      map.put("domain", backOfficeDomain);

      NotificationMessageDTO messageDTO =
          NotificationMessageDTO.builder()
              .destination(user.getEmail())
              .type(MessageType.EMAIL_NOTIFICATION)
              .variables(map)
              .template(Email.ACCOUNT_SUSPENDED_BECAUSE_OF_KYC_MISSING_EMAIL)
              .locale(language.getName())
              .build();

      kafkaProducer.sendNotification(messageDTO);
      //      sendEmailService.sendEmailUsingMustache(emailTemplate, user.getEmail(), map, null);
    }

    //  private void sendHtmlMessageWithAttachment(
    //      String to, String subject, String htmlBody, Map<String, Object> attachments) {
    //    try {
    //      MimeMessage message = javaMailSender.createMimeMessage();
    //      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    //      helper.setFrom(fromEmail, fromName);
    //      helper.setTo(InternetAddress.parse(to));
    //      message.setSubject(subject);
    //
    //      MimeMultipart multipart = new MimeMultipart();
    //
    //      MimeBodyPart messageBodyPart = new MimeBodyPart();
    //      messageBodyPart.setContent(htmlBody, "text/html");
    //      multipart.addBodyPart(messageBodyPart);
    //
    //      ArrayList<Attachment> mailAttachments =
    //          (ArrayList<Attachment>) attachments.get("attachments");
    //      for (Attachment attachment : mailAttachments) {
    //        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
    //        attachmentBodyPart.setDataHandler(
    //            new DataHandler(
    //                new ByteArrayDataSource(attachment.getData(), attachment.getMimeType())));
    //        attachmentBodyPart.setHeader("Content-ID", "<" + attachment.getContentId() + ">");
    //
    //        if (attachment.getMimeType().equalsIgnoreCase("image/png")) {
    //          attachmentBodyPart.setFileName(attachment.getContentId() + ".png");
    //        }
    //        multipart.addBodyPart(attachmentBodyPart);
    //      }
    //      message.setContent(multipart);
    //
    //      helper.setText(htmlBody, true);
    //      javaMailSender.send(message);
    //    } catch (Exception ex) {
    //      log.error("Failed to send mail, Error: {}", ex.getMessage());
    //    }
    //  }
    //    sendEmailService.sendEmailUsingMustache(emailTemplate, user.getEmail(), map, null);
  }

  public void sendEmail(NotificationMessageDTO emailNotificationDTO) {
    if (emailNotificationDTO.getTemplate() == null) {
      throw new BIAPayRuntimeException("Email template is required");
    }
    emailNotificationDTO.setType(MessageType.EMAIL_NOTIFICATION);
    kafkaProducer.sendNotification(emailNotificationDTO);
  }

  public void sendSMS(NotificationMessageDTO smsNotificationDTO) {
    smsNotificationDTO.setType(MessageType.SMS_NOTIFICATION);
    kafkaProducer.sendNotification(smsNotificationDTO);
  }

  @Async("threadPoolTaskExecutor")
  public void sendMFACodeSMS(MFACode mfaCode) {
    log.info("MFA Async sending request {}", mfaCode);

    SMSTemplate smsTemplate =
        smsTemplateRepository
            .findSMSTemplateByTemplateNameAndLocale(mfaCode.getSmsTemplate(), mfaCode.getLocale())
            .orElseThrow(() -> new NotFoundException("SMS Template not found"));
    String message = String.format(smsTemplate.getText(), mfaCode.getMfaCode());

    sendSMS(
        NotificationMessageDTO.builder()
            .destination(mfaCode.getPhoneNumber())
            .message(message)
            .locale(BIAConstants.Language.ENGLISH)
            .build());
  }
}
