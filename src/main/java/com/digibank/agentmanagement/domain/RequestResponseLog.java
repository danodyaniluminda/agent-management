package com.digibank.agentmanagement.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "request_response_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RequestResponseLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "request_id")
  private String requestId;

  @Column(name = "client_ip")
  private String clientIp;

  @Column(name = "client_host")
  private String clientHost;

  @Column(name = "request_uri")
  private String requestUri;

  @Column(name = "request_method")
  private String requestMethod;

  @Column(name = "request_header")
  private String requestHeader;

  @Column(name = "request_payload")
  private String requestPayload;

  @Column(name = "response_header")
  private String responseHeader;

  @Column(name = "response_payload")
  private String responsePayload;

  @Column(name = "status_code")
  private Integer statusCode;

  @Column(name = "insert_date")
  private LocalDateTime insertDate;
}
