package com.biapay.agentmanagement.web.util;


import com.biapay.agentmanagement.web.dto.keycloakutil.LoggedInUser;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;

import static com.biapay.agentmanagement.config.Constants.MOBILE_COUNTRY_CODE;


public class AuthUtil {
  public static String getLoggedInUserUsername(KeycloakAuthenticationToken token) {
    return ((KeycloakPrincipal) token.getPrincipal())
        .getKeycloakSecurityContext()
        .getToken()
        .getPreferredUsername();
  }

  public static String loggedInIAMId(KeycloakAuthenticationToken token) {
    return token.getPrincipal().toString();
  }

  public static LoggedInUser getLoggedInUser(KeycloakAuthenticationToken token) {
    AccessToken accessToken =
        ((KeycloakPrincipal) token.getPrincipal()).getKeycloakSecurityContext().getToken();
    return LoggedInUser.builder()
        .username(accessToken.getPreferredUsername())
        .mobileCountryCode(
            accessToken.getOtherClaims().get(MOBILE_COUNTRY_CODE) != null
                ? accessToken.getOtherClaims().get(MOBILE_COUNTRY_CODE).toString()
                : null)
        .iamId(loggedInIAMId(token))
        .build();
  }
}
