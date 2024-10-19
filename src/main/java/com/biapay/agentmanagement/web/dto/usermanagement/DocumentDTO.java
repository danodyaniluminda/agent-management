package com.biapay.agentmanagement.web.dto.usermanagement;


import com.biapay.agentmanagement.domain.IDDocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DocumentDTO {
    private Long documentId;

    private String originalFileName;

    private String fileName;

    @Enumerated(EnumType.STRING)
    private IDDocumentType documentType;
}
