package com.biapay.agentmanagement.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class KeycloakProperties {

  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.auth-server-url}")
  private String authServerUrl;

  @Value("${keycloak.resource}")
  private String resource;

  @Value("${keycloak.credentials.secret}")
  private String secret;

  @Value("${keycloak-config.admin.username}")
  private String adminUsername;

  @Value("${keycloak-config.admin.password}")
  private String adminPassword;
}
