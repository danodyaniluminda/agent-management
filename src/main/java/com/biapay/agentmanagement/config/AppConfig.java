package com.biapay.agentmanagement.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfig {
  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration:900000}")
  private Long jwtExpiration;

  @Value("${authentication.max.login.attempt:3}")
  private Integer maxLoginAttempts;

  @Value("${backoffice.domain}")
  private String backofficeUrl;

  @Value("${super-merchant.email}")
  private String superMerchantEmail;

  @Value("${super-merchant.password}")
  private String superMerchantPassword;
}
