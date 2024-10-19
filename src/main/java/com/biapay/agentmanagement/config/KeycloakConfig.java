package com.biapay.agentmanagement.config;

import lombok.NoArgsConstructor;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor
public class KeycloakConfig {

  private static Keycloak instance;

  public static Keycloak getInstance(KeycloakProperties keycloakProperties) {
    if (instance == null) {
      instance =
          KeycloakBuilder.builder()
              .serverUrl(keycloakProperties.getAuthServerUrl())
              .realm(keycloakProperties.getRealm())
              .grantType(OAuth2Constants.PASSWORD)
              .username(keycloakProperties.getAdminUsername())
              .password(keycloakProperties.getAdminPassword())
              .clientId(keycloakProperties.getResource())
              .clientSecret(keycloakProperties.getSecret())
              .resteasyClient(
                  new ResteasyClientBuilder()
                      .connectionPoolSize(10)
                      .register(new CustomJacksonProvider())
                      .build())
              .build();
    }
    return instance;
  }
}
