package com.biapay.agentmanagement.web.dto.packagemanagement;

import lombok.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ScreenDto {

    private Long screenId;
    @NonNull
    private String name;
    private String description;
}
