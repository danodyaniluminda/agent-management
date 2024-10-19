package com.biapay.agentmanagement.utils;

import com.biapay.agentmanagement.config.KeycloakConfig;
import com.biapay.agentmanagement.config.KeycloakProperties;
import com.biapay.core.constant.BIAConstants;
import com.biapay.core.exception.BIAPayRuntimeException;
import com.biapay.core.exception.InvalidRequestException;
import com.biapay.core.exception.NotFoundException;
import com.biapay.core.model.KeycloakGroup;
import com.biapay.core.model.KeycloakRole;
import com.biapay.core.model.UserType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUtil {

  private final KeycloakProperties keycloakProperties;

  public void createUser(
      String username,
      String password,
      String firstName,
      String lastName,
      String email,
      UserType userType,
      List<String> groups) {
    RealmResource realm =
        KeycloakConfig.getInstance(keycloakProperties).realm(keycloakProperties.getRealm());
    UsersResource usersResource = realm.users();
    CredentialRepresentation credentialRepresentation = createPasswordCredentials(password);
    UserRepresentation kcUser = new UserRepresentation();
    kcUser.setUsername(userType + "_" + username);
    if (email != null) {
      kcUser.setEmail(userType + "_" + email);
    }
    kcUser.setEmailVerified(false);
    kcUser.setFirstName(firstName);
    kcUser.setLastName(lastName);
    // Add the user to the ROLE_MERCHANT group
    kcUser.setGroups(groups);
    kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
    kcUser.setEnabled(true);
    Response response = usersResource.create(kcUser);
    log.info(
        "User Created, response: {}, {}, {}",
        response.getStatus(),
        response.getStatusInfo().getReasonPhrase(),
        response.getStatusInfo().getFamily().name());
    if (response.getStatus() >= 400) {
      if (response.getStatus() == 401) {
        throw new InvalidRequestException(
            "Please provide authorization or the provided authorization is invalid!");
      }
      if (response.getStatus() == 403) {
        throw new InvalidRequestException(
            "The authorization provided by the client is not enough to access the resource!");
      }
      if (response.getStatus() == 409) {
        throw new InvalidRequestException(
            "The resource the client is trying to create already exists or some conflict when processing the request!");
      }

      throw new InvalidRequestException("Given password doesn't match our password policy.");
    }
  }

  public void updateUser(
      String userPrefix,
      String username,
      String password,
      boolean changePassword,
      String firstName,
      String lastName,
      List<String> groups) {
    RealmResource realm =
        KeycloakConfig.getInstance(keycloakProperties).realm(keycloakProperties.getRealm());
    UsersResource usersResource = realm.users();
    List<UserRepresentation> userRepresentations =
        usersResource.search(userPrefix + "_" + username, true);
    if (userRepresentations != null && !userRepresentations.isEmpty()) {
      UserRepresentation userRep = userRepresentations.get(0);
      UserResource userResource = usersResource.get(userRep.getId());
      if (changePassword) {
        userResource.resetPassword(createPasswordCredentials(password));
      }
      userRep.setFirstName(firstName);
      userRep.setLastName(lastName);
      userRep.setGroups(groups);
      userResource.update(userRep);
    }
  }

  public void resetPassword(String username, String password) {
    RealmResource realm =
        KeycloakConfig.getInstance(keycloakProperties).realm(keycloakProperties.getRealm());
    UsersResource usersResource = realm.users();
    List<UserRepresentation> userRepresentations = usersResource.search(username, true);
    if (userRepresentations != null && !userRepresentations.isEmpty()) {
      UserRepresentation userRep = userRepresentations.get(0);
      UserResource userResource = usersResource.get(userRep.getId());
      userResource.resetPassword(createPasswordCredentials(password));
    }
  }

  public void createGroup(String name, KeycloakGroup keycloakGroup) {
    RealmResource realm =
        KeycloakConfig.getInstance(keycloakProperties).realm(keycloakProperties.getRealm());
    GroupRepresentation groupRepresentation = new GroupRepresentation();
    groupRepresentation.setName(name);
    Response response = realm.groups().add(groupRepresentation);
    log.info(
        "Response: status: {}, Message: {}",
        response.getStatus(),
        response.getStatusInfo().getReasonPhrase());
    if (response.getStatus() >= 400) {
      if (response.getStatus() == 409) {
        throw new InvalidRequestException(
            "Group with the same name exists, kindly change the name.");
      }
      throw new BIAPayRuntimeException("Error while creating");
    }
    for (GroupRepresentation group : realm.groups().groups()) {
      if (group.getName().equalsIgnoreCase(name)) {
        groupRepresentation = group;
        break;
      }
    }
    addRolesToGroup(keycloakGroup, realm, realm.groups().group(groupRepresentation.getId()));
  }

  public void updateGroup(String groupId, String name, KeycloakGroup keycloakGroup) {

    RealmResource realm =
        KeycloakConfig.getInstance(keycloakProperties).realm(keycloakProperties.getRealm());
    GroupResource group = realm.groups().group(groupId);
    ValidateGroupAction(group);
    GroupRepresentation representation = group.toRepresentation();
    representation.setName(name);
    group.update(representation);
    group.roles().realmLevel().remove(group.roles().realmLevel().listAll());

    addRolesToGroup(keycloakGroup, realm, group);
  }

  public void deleteGroup(String groupId, String namePrefix, boolean validateName) {
    RealmResource realm =
        KeycloakConfig.getInstance(keycloakProperties).realm(keycloakProperties.getRealm());
    GroupResource group = realm.groups().group(groupId);
    ValidateGroupAction(group);
    if (validateName) {
      if (!group.toRepresentation().getName().startsWith(namePrefix)) {
        throw new InvalidRequestException("Group not found or not associated with this user.");
      }
    }
    group.remove();
  }

  private static void ValidateGroupAction(GroupResource group) {
    if (group.toRepresentation().getName().equals(BIAConstants.Role.ROLE_ADMIN)
        || group.toRepresentation().getName().equals(BIAConstants.Role.ROLE_MAKER)
        || group.toRepresentation().getName().equals(BIAConstants.Role.ROLE_CHECKER)
        || group.toRepresentation().getName().equals(BIAConstants.Role.ROLE_USER)
        || group.toRepresentation().getName().equals(BIAConstants.Role.ROLE_MERCHANT)) {
      throw new IllegalArgumentException("Illegal action.");
    }
  }

  public KeycloakGroup groupRoles(String groupName) {
    RealmResource realm =
        KeycloakConfig.getInstance(keycloakProperties).realm(keycloakProperties.getRealm());
    KeycloakGroup keycloakGroup = new KeycloakGroup();
    List<GroupRepresentation> groups = realm.groups().groups(groupName, 0, 1);
    if (groups != null && !groups.isEmpty()) {
      GroupRepresentation merchantGroup = groups.get(0);
      MappingsRepresentation representation =
          realm.groups().group(merchantGroup.getId()).roles().getAll();
      List<KeycloakRole> keycloakRoleList = new ArrayList<>();
      if (representation.getRealmMappings() != null
          && !representation.getRealmMappings().isEmpty()) {
        for (RoleRepresentation realmMapping : representation.getRealmMappings()) {
          keycloakRoleList.add(
              KeycloakRole.builder()
                  .id(realmMapping.getId())
                  .name(realmMapping.getName())
                  .description(realmMapping.getDescription())
                  .build());
        }
      }
      keycloakGroup.setId(merchantGroup.getId());
      keycloakGroup.setName(merchantGroup.getName());
      keycloakGroup.setRoles(keycloakRoleList);
      return keycloakGroup;
    }
    throw new NotFoundException("Group Not Found");
  }

  public List<KeycloakGroup> listAllGroups() {
    RealmResource realm =
        KeycloakConfig.getInstance(keycloakProperties).realm(keycloakProperties.getRealm());
    List<KeycloakGroup> keycloakGroups = new ArrayList<>();
    for (GroupRepresentation group : realm.groups().groups()) {
      List<KeycloakRole> keycloakRoleList = roleList(realm, group);
      keycloakGroups.add(
          KeycloakGroup.builder()
              .id(group.getId())
              .name(group.getName())
              .roles(keycloakRoleList)
              .build());
    }
    return keycloakGroups;
  }

  /**
   * Using the name prefix for getting merchant owned groups which is created By using their
   * merchant id and _ as a prefix
   */
  public List<KeycloakGroup> listGroupsByNamePrefix(String namePrefix) {
    RealmResource realm =
        KeycloakConfig.getInstance(keycloakProperties).realm(keycloakProperties.getRealm());
    List<KeycloakGroup> keycloakGroups = new ArrayList<>();
    for (GroupRepresentation group : realm.groups().groups()) {
      if (group.getName().startsWith(namePrefix)) {
        List<KeycloakRole> keycloakRoleList = roleList(realm, group);
        keycloakGroups.add(
            KeycloakGroup.builder()
                .id(group.getId())
                .name(group.getName().substring((namePrefix).length()))
                .roles(keycloakRoleList)
                .build());
      }
    }
    return keycloakGroups;
  }

  public List<KeycloakGroup> listAllNonMerchantGroups() {
    RealmResource realm =
        KeycloakConfig.getInstance(keycloakProperties).realm(keycloakProperties.getRealm());
    List<KeycloakGroup> keycloakGroups = new ArrayList<>();
    for (GroupRepresentation group : realm.groups().groups()) {
      if (!group.getName().matches("^\\d_.*")) {
        List<KeycloakRole> keycloakRoleList = roleList(realm, group);
        keycloakGroups.add(
            KeycloakGroup.builder()
                .id(group.getId())
                .name(group.getName())
                .roles(keycloakRoleList)
                .build());
      }
    }
    return keycloakGroups;
  }

  public KeycloakGroup groupById(String groupId) {
    RealmResource realm =
        KeycloakConfig.getInstance(keycloakProperties).realm(keycloakProperties.getRealm());
    GroupResource group = realm.groups().group(groupId);
    if (group == null) {
      throw new NotFoundException("Group not found");
    }
    GroupRepresentation representation = group.toRepresentation();
    List<KeycloakRole> keycloakRoleList = roleList(realm, representation);
    return KeycloakGroup.builder()
        .id(representation.getId())
        .name(representation.getName())
        .roles(keycloakRoleList)
        .build();
  }

  private static List<KeycloakRole> roleList(RealmResource realm, GroupRepresentation group) {
    List<KeycloakRole> keycloakRoleList = new ArrayList<>();
    MappingsRepresentation representation = realm.groups().group(group.getId()).roles().getAll();
    if (representation.getRealmMappings() != null && !representation.getRealmMappings().isEmpty()) {
      for (RoleRepresentation realmMapping : representation.getRealmMappings()) {
        keycloakRoleList.add(
            KeycloakRole.builder()
                .id(realmMapping.getId())
                .name(realmMapping.getName())
                .description(realmMapping.getDescription())
                .build());
      }
    }
    return keycloakRoleList;
  }

  public void addRolesToGroup(
      KeycloakGroup keycloakGroup, RealmResource realm, GroupResource group) {
    List<RoleRepresentation> rolesToAdd = new ArrayList<>();
    for (KeycloakRole role : keycloakGroup.getRoles()) {
      for (RoleRepresentation roleRepresentation : realm.roles().list()) {
        if (roleRepresentation.getId().equals(role.getId())) {
          rolesToAdd.add(roleRepresentation);
        }
      }
    }
    group.roles().realmLevel().add(rolesToAdd);
  }

  private static CredentialRepresentation createPasswordCredentials(String password) {
    CredentialRepresentation passwordCredentials = new CredentialRepresentation();
    passwordCredentials.setTemporary(false);
    passwordCredentials.setType(CredentialRepresentation.PASSWORD);
    passwordCredentials.setValue(password);
    return passwordCredentials;
  }
}
