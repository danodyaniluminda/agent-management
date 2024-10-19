package com.biapay.agentmanagement.service;

import com.biapay.core.constant.MessageType;
import com.biapay.core.dto.NotificationMessageDTO;
import com.biapay.core.dto.StreamMessageDTO;
import com.biapay.core.model.Merchant;
import com.biapay.core.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.topics.notification}")
  private String notificationTopic;

  @Value("${spring.kafka.topics.account-management}")
  private String accountManagementTopic;

  @Autowired private ObjectMapper objectMapper;

  public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendNotification(NotificationMessageDTO notificationMessageDTO) {
    try {
      log.info("Streaming the message to kafka");
      this.kafkaTemplate.send(notificationTopic, JsonUtil.toJsonString(notificationMessageDTO));
    } catch (Exception e) {
      log.error("Could not send message to notification service. {}", e.getMessage());
    }
    log.info("Sending notification: {} to notification service", "");
  }

  public void createAccount(Merchant merchant) {
    log.info(
        "Processing createAccount streaming event to kafka for: {}",
        merchant.getRootUser().getUserId());

    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("accountUserType", merchant.getRootUser().getUserType());
      jsonObject.put("accountUserId", merchant.getRootUser().getUserId());

      StreamMessageDTO streamMessageDTO =
          StreamMessageDTO.builder()
              .type(MessageType.ACCOUNT_CREATION_REQUEST)
              .payload(jsonObject.toString())
              .build();

      this.kafkaTemplate.send(accountManagementTopic, JsonUtil.toJsonString(streamMessageDTO));
    } catch (Exception e) {
      log.error("Could not send message to notification service. {}", e.getMessage());
    }
    log.info("Sending instruction: {} to account service", "TO_ACCOUNT_MANAGEMENT");
  }
}
