package com.biapay.agentmanagement.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DocumentIdInfoDTO {


    private Long documentId;

    private String documentName;

    private String documentType;

    private String documentIdNumber;

    private LocalDate documentExpiryDate;

    private String documentFileName;

    private String documentFileNameOrignal;


}
