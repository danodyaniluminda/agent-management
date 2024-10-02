package com.digibank.agentmanagement.web.dto.packagemanagement.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ScreenPermission {

    private Boolean add;
    private Boolean update;
    private Boolean delete;
    private Boolean view;
    private Boolean list;
    private Long screenId;
}
