package com.digibank.agentmanagement.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AgentOperationDto implements Serializable {

  private static final long serialVersionUID = 584861899224854004L;
  private Long id;
  private Long operationId;
  private String name;
  private Boolean active;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createDate;
  private Integer lowerBound;
  private Integer upperBound;
}
