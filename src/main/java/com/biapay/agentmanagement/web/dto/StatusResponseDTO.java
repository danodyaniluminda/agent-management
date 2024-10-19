package com.biapay.agentmanagement.web.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusResponseDTO<T> {

  private String status;
  private String message;
  private T data;
}
