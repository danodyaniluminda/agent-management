package com.digibank.agentmanagement.service;

import com.digibank.agentmanagement.config.Constants;
import com.digibank.agentmanagement.domain.AgentDetails;
import com.digibank.agentmanagement.domain.UserType;
import com.digibank.agentmanagement.exception.DigibankRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.digibank.agentmanagement.config.Constants.MOBILE_COUNTRY_CODE;
import static com.digibank.agentmanagement.config.Constants.USER_ID;
import static com.digibank.agentmanagement.config.Constants.USER_TYPE;

@Service
@Slf4j
public class KeycloakService {
  private Keycloak keycloak = null;

  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.auth-server-url}")
  private String authServerURL;

  @Value("${keycloak.resource}")
  private String clientId;

  @Value("${keycloak.credentials.secret}")
  private String clientSecret;

  @PostConstruct
  public void init() {
    keycloak =
        KeycloakBuilder.builder()
            .serverUrl(authServerURL)
            .realm(realm)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build();
  }

  private List<RoleRepresentation> rolesToRealmRoleRepresentation(List<String> roles ) {

    log.info("Roles {}" , roles);
    List<RoleRepresentation> existingRoles = keycloak.realm(realm)
            .roles()
            .list();

    List<String> serverRoles = existingRoles
            .stream()
            .map(RoleRepresentation::getName)
            .collect(Collectors.toList());
    List<RoleRepresentation> resultRoles = new ArrayList<>();

    for (String role : roles) {
      int index = serverRoles.indexOf(role);
      if (index != -1) {
        resultRoles.add(existingRoles.get(index));
      } else {
        log.info("Role doesn't exist");
      }
    }
    return resultRoles;
  }
  public String createAgentUser(String username, String phoneNumberCountryCode, String email, String realmRole , Long agentId){
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setUsername(username);
    userRepresentation.setEmail(email);
    userRepresentation.setEnabled(false);
    userRepresentation.setEmailVerified(false);
//    Map<String, List<String>> userAttributes =
//        Map.of(MOBILE_COUNTRY_CODE, Stream.of(phoneNumberCountryCode).collect(Collectors.toList()));
//
    Map<String, List<String>> userAttributes = new HashMap<>();

    userAttributes.put(USER_ID, Stream.of(agentId.toString()).collect(Collectors.toList()));
    userAttributes.put(
            USER_TYPE, Stream.of(UserType.ADMIN.toString()).collect(Collectors.toList()));

    userRepresentation.setAttributes(userAttributes);
    //userRepresentation.setRealmRoles(  rolesToRealmRoleRepresentation (rolesList));

    Response result = keycloak.realm(realm).users().create(userRepresentation);
    if (result.getStatus() != 201) {
      log.error("Resposne code: {}", result.getStatus());
      throw new DigibankRuntimeException("Error while creating user");
    }
    String userId = keycloak.realm(realm).users().search(username).get(0).getId();


    addRealmRolesToKeycloakUser(userId, realmRole);

    return userId;
  }
  public String createKeycloakUser(
      String username, String phoneNumberCountryCode, String email, String realmRole , Long agentId) {


    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setUsername(username);
    userRepresentation.setEmail(email);
    userRepresentation.setEnabled(false);
    userRepresentation.setEmailVerified(true);
//    Map<String, List<String>> userAttributes =
//        Map.of(MOBILE_COUNTRY_CODE, Stream.of(phoneNumberCountryCode).collect(Collectors.toList()));
//
    Map<String, List<String>> userAttributes = new HashMap<>();
    userAttributes.put(
            MOBILE_COUNTRY_CODE, Stream.of(phoneNumberCountryCode).collect(Collectors.toList()));
    userAttributes.put(USER_ID, Stream.of(agentId.toString()).collect(Collectors.toList()));
    userAttributes.put(
            USER_TYPE, Stream.of(UserType.AGENT.toString()).collect(Collectors.toList()));

    userRepresentation.setAttributes(userAttributes);
    //userRepresentation.setRealmRoles(  rolesToRealmRoleRepresentation (rolesList));

    Response result = keycloak.realm(realm).users().create(userRepresentation);
    if (result.getStatus() != 201) {
      log.error("Resposne code: {}", result.getStatus());
      throw new DigibankRuntimeException("Error while creating user");
    }
    String userId = keycloak.realm(realm).users().search(username).get(0).getId();


    addRealmRolesToKeycloakUser(userId, realmRole);

    return userId;
  }

  public void enableUserCore(String userEmail) {
    UserResource userResource = getKeycloakUserByUsername(userEmail);
    UserRepresentation userRepresentation = userResource.toRepresentation();
    userRepresentation.setEnabled(true);
    userResource.update(userRepresentation);
  }

  public void enableUser(AgentDetails agentDetails) {
    UserResource userResource = getKeycloakUserByUsername(agentDetails.getPhoneNo());
    UserRepresentation userRepresentation = userResource.toRepresentation();
    userRepresentation.setEnabled(true);
    userResource.update(userRepresentation);
  }
//
  public void deleteUser(String email) {
    UserResource userResource = getKeycloakUserByUsername(email);
    if (userResource != null) {
      userResource.remove();
    }
  }

  public void addRealmRolesToKeycloakUser(String userId, String role) {
    // Retrieve User
    UserResource userResource = keycloak.realm(realm).users().get(userId);
//    RoleRepresentation roleRepresentation =
//        keycloak.realm(realm).roles().get(role).toRepresentation();
    List<String> rolesList=new ArrayList<String>();
    rolesList.add(role);
    rolesList.add("ROLE_CUSTOMER");

    //userRepresentation.setRealmRoles(  rolesToRealmRoleRepresentation (rolesList));

    userResource.roles().realmLevel().add(rolesToRealmRoleRepresentation (rolesList));
  }

  public void resetKeycloakUserPassword(String username, String password) {
    CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(password);
    credential.setTemporary(false);

    // Added for multi channel handling with same phone number
    username = "a_"+username;

    UserResource userResource = getKeycloakUserByUsername(username);
    UserRepresentation userRepresentation = userResource.toRepresentation();
    userRepresentation.setCredentials(Arrays.asList(credential));
    userResource.update(userRepresentation);
  }

  public void resetKeycloakUserPasswordCore(String username, String password) {
    CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(password);
    credential.setTemporary(false);



    UserResource userResource = getKeycloakUserByUsername(username);
    UserRepresentation userRepresentation = userResource.toRepresentation();
    userRepresentation.setCredentials(Arrays.asList(credential));
    userResource.update(userRepresentation);
  }

  public UserResource getKeycloakUserByUsername(String username) {
   try{
     List<UserRepresentation> userRepresentationList =
         keycloak.realm(realm).users().search(username);
     if (userRepresentationList.size() > 0) {
       String userId = userRepresentationList.get(0).getId();
       UserResource userResource = keycloak.realm(realm).users().get(userId);
       return userResource;
     } else {
       return null;
     }
   } catch (Exception e){
     log.error("Failed to validate from keycloak", e);
     return null;
   }
  }

  public boolean isUserExistByLogin(String email) {
    UserResource userResource = getKeycloakUserByUsername(email);
    if (userResource != null) {
      return true;
    } else {
      return false;
    }
  }

  public void updateEmail(AgentDetails agentDetails) {
    UserResource userResource =
            getKeycloakUserByUsername(
                    Constants.KEYCLOAK_AGENT_PREFIX + agentDetails.getPhoneNo());
    UserRepresentation userRepresentation = userResource.toRepresentation();
    userRepresentation.setEmail(agentDetails.getAgentEmailAddress());
    userRepresentation.setEmailVerified(true);
    userRepresentation.setEnabled(true);
    userResource.update(userRepresentation);
  }

  public void updatePhoneNumber(String existingPhoneNumber, AgentDetails agentDetails) {
    UserResource userResource =
            getKeycloakUserByUsername(Constants.KEYCLOAK_AGENT_PREFIX + existingPhoneNumber);
    UserRepresentation userRepresentation = userResource.toRepresentation();
    userRepresentation.setUsername(
            Constants.KEYCLOAK_AGENT_PREFIX + agentDetails.getPhoneNo());
    userRepresentation
            .getAttributes()
            .put(
                    MOBILE_COUNTRY_CODE,
                    Stream.of(agentDetails.getPhoneNumberCountryCode())
                            .collect(Collectors.toList()));
    userResource.update(userRepresentation);
  }



//  public void createKeycloakClient(String clientId, String clientSecret) {
//    ClientRepresentation clientRepresentation = new ClientRepresentation();
//    clientRepresentation.setClientId(clientId);
//    clientRepresentation.setProtocol("openid-connect");
//    clientRepresentation.setSecret(clientSecret);
//    clientRepresentation.setRedirectUris(Arrays.asList("http://localhost:9003/"));
//    clientRepresentation.setServiceAccountsEnabled(true);
//    clientRepresentation.setPublicClient(false);
//    Response result = keycloak.realm(realm).clients().create(clientRepresentation);
//    if (result.getStatus() != 201) {
//      log.error("Error while creating client. Resposne code: {}", result.getStatus());
//      throw new SARARuntimeException("Error while creating client");
//    }
//
//    String clientUUID = keycloak.realm(realm).clients().findByClientId(clientId).get(0).getId();
//    addRealmRolesToKeycloakUser(
//        keycloak.realm(realm).clients().get(clientUUID).getServiceAccountUser().getId(),
//        "merchant_application");
//  }
}
